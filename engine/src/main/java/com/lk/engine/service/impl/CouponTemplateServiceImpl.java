package com.lk.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lk.engine.common.constant.EngineRedisConstant;
import com.lk.engine.common.enums.CouponTemplateStatusEnum;
import com.lk.engine.dao.entity.CouponTemplateDO;
import com.lk.engine.dao.mapper.CouponTemplateMapper;
import com.lk.engine.dao.sharding.DBShardingUtil;
import com.lk.engine.dto.req.CouponTemplateQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.lk.engine.service.CouponTemplateService;
import com.lk.framework.exception.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Service实现
 * @createDate 2024-09-06 16:54:12
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplateDO>
        implements CouponTemplateService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RBloomFilter<String> couponTemplateQueryBloomFilter;
    private final RedissonClient redissonClient;
    private final CouponTemplateMapper couponTemplateMapper;

    @Override
    public CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam) {
        // 查询 Redis 缓存中是否存在优惠券模板信息
        String couponTemplateCacheKey = String.format(EngineRedisConstant.COUPON_TEMPLATE_KEY, requestParam.getCouponTemplateId());
        Map<Object, Object> couponTemplateCacheMap = stringRedisTemplate.opsForHash().entries(couponTemplateCacheKey);

        // 如果存在直接返回，不存在需要通过布隆过滤器、缓存空值以及双重判定锁的形式读取数据库中的记录
        if (MapUtil.isEmpty(couponTemplateCacheMap)) {
            // 判断布隆过滤器是否存在指定模板 ID，不存在直接返回错误
            if (!couponTemplateQueryBloomFilter.contains(requestParam.getCouponTemplateId())) {
                throw new ClientException("优惠券模板不存在");
            }
        }

        // 查询 Redis 缓存中是否存在优惠券模板空值信息，如果有代表模板不存在，直接返回
        String couponTemplateIsNullCacheKey = String.format(EngineRedisConstant.COUPON_TEMPLATE_IS_NULL_KEY, requestParam.getCouponTemplateId());
        Boolean hasKeyFlag = stringRedisTemplate.hasKey(couponTemplateIsNullCacheKey);
        if (Boolean.TRUE.equals(hasKeyFlag)) {
            throw new ClientException("优惠券模板不存在");
        }

        // 获取优惠券模板分布式锁
        RLock lock = redissonClient.getLock(String.format(EngineRedisConstant.LOCK_COUPON_TEMPLATE_KEY, requestParam.getCouponTemplateId()));
        lock.lock();

        try {
            // 双重判定空值缓存是否存在，存在则继续抛异常
            hasKeyFlag = stringRedisTemplate.hasKey(couponTemplateIsNullCacheKey);
            if (Boolean.TRUE.equals(hasKeyFlag)) {
                throw new ClientException("优惠券模板不存在");
            }

            // 通过双重判定锁优化大量请求无意义查询数据库
            couponTemplateCacheMap = stringRedisTemplate.opsForHash().entries(couponTemplateCacheKey);
            if (MapUtil.isEmpty(couponTemplateCacheMap)) {
                LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                        .eq(CouponTemplateDO::getShopNumber, Long.parseLong(requestParam.getShopNumber()))
                        .eq(CouponTemplateDO::getId, Long.parseLong(requestParam.getCouponTemplateId()))
                        .eq(CouponTemplateDO::getStatus, CouponTemplateStatusEnum.ACTIVE.getStatus());
                CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOne(queryWrapper);

                // 优惠券模板不存在或者已过期加入空值缓存，并且抛出异常
                if (couponTemplateDO == null) {
                    stringRedisTemplate.opsForValue().set(couponTemplateIsNullCacheKey, "", 30, TimeUnit.MINUTES);
                    throw new ClientException("优惠券模板不存在或已过期");
                }

                // 通过将数据库的记录序列化成 JSON 字符串放入 Redis 缓存
                CouponTemplateQueryRespDTO actualRespDTO = BeanUtil.toBean(couponTemplateDO, CouponTemplateQueryRespDTO.class);
                Map<String, Object> cacheTargetMap = BeanUtil.beanToMap(actualRespDTO, false, true);

                Map<String, String> actualCacheTargetMap = cacheTargetMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                        ));

                // 通过 LUA 脚本执行设置 Hash 数据以及设置过期时间
                String luaScript = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
                        "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";

                List<String> keys = Collections.singletonList(couponTemplateCacheKey);
                List<String> args = new ArrayList<>(actualCacheTargetMap.size() * 2 + 1);
                actualCacheTargetMap.forEach((key, value) -> {
                    args.add(key);
                    args.add(value);
                });

                // 优惠券活动过期时间转换为秒级别的 Unix 时间戳
                args.add(String.valueOf(couponTemplateDO.getValidEndTime().getTime() / 1000));

                // 执行lua脚本
                stringRedisTemplate.execute(
                        new DefaultRedisScript<>(luaScript, Long.class),
                        keys,
                        args.toArray()
                );

                couponTemplateCacheMap = cacheTargetMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            }

        } finally {
            lock.unlock();
        }
        return BeanUtil.mapToBean(couponTemplateCacheMap, CouponTemplateQueryRespDTO.class, false, CopyOptions.create());
    }

    @Override
    public List<CouponTemplateDO> listCouponTemplateById(List<Long> couponTemplateIds, List<Long> shopNumbers) {
        // 1. 将 shopNumbers集合 对应的index拆分到数据库中
        Map<Integer, List<Integer>> databaseIndexMap = splitIndexByDatabase(shopNumbers);

        List<CouponTemplateDO> result = new ArrayList<>();
        // 2. 对每个数据库执行查询
        for (List<Integer> index : databaseIndexMap.values()) {
            // 执行查询
            List<CouponTemplateDO> couponTemplateDOS = queryDatabase(couponTemplateIds, shopNumbers, index);
            result.addAll(couponTemplateDOS);
        }
        return result;
    }

    private List<CouponTemplateDO> queryDatabase(List<Long> couponTemplateIds, List<Long> shopNumbers, List<Integer> index) {
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .and(wrapper -> {
                    for (int i : index) {
                        wrapper.or(innerWrapper -> innerWrapper.eq(CouponTemplateDO::getShopNumber, shopNumbers.get(i))
                                .eq(CouponTemplateDO::getId, couponTemplateIds.get(i)));
                    }
                });
        return couponTemplateMapper.selectList(queryWrapper);
    }

    private Map<Integer, List<Integer>> splitIndexByDatabase(List<Long> shopNumbers) {
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < shopNumbers.size(); i++) {
            int databaseMod = DBShardingUtil.doUserCouponSharding(shopNumbers.get(i));
            map.computeIfAbsent(databaseMod, k -> new ArrayList<>()).add(i);
        }

        return map;
    }
}




