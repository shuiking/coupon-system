package com.lk.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lk.engine.dao.entity.CouponTemplateDO;
import com.lk.engine.dao.entity.CouponTemplateRemindDO;
import com.lk.engine.dao.mapper.CouponTemplateRemindMapper;
import com.lk.engine.dto.req.CouponTemplateRemindCancelReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.lk.engine.mq.event.CouponRemindEvent;
import com.lk.engine.mq.producer.CouponRemindProducer;
import com.lk.engine.service.CouponTemplateRemindService;
import com.lk.engine.service.CouponTemplateService;
import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;
import com.lk.engine.toolkit.CouponTemplateRemindUtil;
import com.lk.framework.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lk.engine.common.constant.EngineRedisConstant.USER_COUPON_TEMPLATE_REMIND_INFORMATION;

/**
 * @author k
 * @description 针对表【t_coupon_template_remind(用户预约提醒信息存储表)】的数据库操作Service实现
 * @createDate 2024-09-06 16:54:12
 */

@Service
@RequiredArgsConstructor
public class CouponTemplateRemindServiceImpl extends ServiceImpl<CouponTemplateRemindMapper, CouponTemplateRemindDO>
        implements CouponTemplateRemindService {

    private final CouponTemplateRemindMapper couponTemplateRemindMapper;
    private final CouponTemplateService couponTemplateService;
    private final RBloomFilter<String> cancelRemindBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponRemindProducer couponRemindProducer;

    @Override
    @Transactional
    public boolean createCouponRemind(CouponTemplateRemindCreateReqDTO requestParam) {
        LambdaQueryWrapper<CouponTemplateRemindDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateRemindDO.class)
                .eq(CouponTemplateRemindDO::getUserId, requestParam.getUserId())
                .eq(CouponTemplateRemindDO::getCouponTemplateId, requestParam.getCouponTemplateId());
        CouponTemplateRemindDO couponTemplateRemindDO = couponTemplateRemindMapper.selectOne(queryWrapper);

        if (couponTemplateRemindDO == null) {
            couponTemplateRemindDO = BeanUtil.toBean(requestParam, CouponTemplateRemindDO.class);
            couponTemplateRemindDO.setInformation(CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType()));
            couponTemplateRemindMapper.insert(couponTemplateRemindDO);
        } else {
            Long information = couponTemplateRemindDO.getInformation();
            Long bitMap = CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType());
            if ((information & bitMap) != 0L) {
                throw new ClientException("已经创建过该提醒了");
            }
            couponTemplateRemindDO.setInformation(information ^ bitMap);
            couponTemplateRemindMapper.update(couponTemplateRemindDO, queryWrapper);
        }

        couponRemindProducer.sendMessage(BeanUtil.toBean(requestParam, CouponRemindEvent.class));
        return true;
    }

    @Override
    public List<CouponTemplateRemindQueryRespDTO> listCouponRemind(CouponTemplateRemindQueryReqDTO requestParam) {
        String value = stringRedisTemplate.opsForValue().get(String.format(USER_COUPON_TEMPLATE_REMIND_INFORMATION, requestParam.getUserId()));

        if (value != null) {
            return JSON.parseArray(value, CouponTemplateRemindQueryRespDTO.class);
        }
        LambdaQueryWrapper<CouponTemplateRemindDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateRemindDO.class)
                .eq(CouponTemplateRemindDO::getUserId, requestParam.getUserId());

        // 查出用户预约的信息
        List<CouponTemplateRemindDO> couponTemplateRemindDOS = couponTemplateRemindMapper.selectList(queryWrapper);
        if (couponTemplateRemindDOS == null || couponTemplateRemindDOS.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据优惠券 ID 查询优惠券信息
        List<Long> couponIds = couponTemplateRemindDOS.stream().map(CouponTemplateRemindDO::getCouponTemplateId).collect(Collectors.toList());
        List<Long> shopNumbers = couponTemplateRemindDOS.stream().map(CouponTemplateRemindDO::getShopNumber).collect(Collectors.toList());
        List<CouponTemplateDO> couponTemplateDOS = couponTemplateService.listCouponTemplateById(couponIds, shopNumbers);
        List<CouponTemplateRemindQueryRespDTO> resp = BeanUtil.copyToList(couponTemplateDOS, CouponTemplateRemindQueryRespDTO.class);

        resp.forEach(each -> {
            // 找到当前优惠券对应的预约提醒信息
            couponTemplateRemindDOS.stream()
                    .filter(i -> i.getCouponTemplateId().equals(each.getId()))
                    .findFirst()
                    // 解析并填充预约提醒信息
                    .ifPresent(i -> CouponTemplateRemindUtil.fillRemindInformation(each, i.getInformation()));
        });
        stringRedisTemplate.opsForValue().set(String.format(USER_COUPON_TEMPLATE_REMIND_INFORMATION, requestParam.getUserId()), JSON.toJSONString(resp), 1, TimeUnit.MINUTES);
        return resp;
    }

    @Override
    @Transactional
    public boolean cancelCouponRemind(CouponTemplateRemindCancelReqDTO requestParam) {
        LambdaQueryWrapper<CouponTemplateRemindDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateRemindDO.class)
                .eq(CouponTemplateRemindDO::getUserId, requestParam.getUserId())
                .eq(CouponTemplateRemindDO::getCouponTemplateId, requestParam.getCouponTemplateId());
        CouponTemplateRemindDO couponTemplateRemindDO = couponTemplateRemindMapper.selectOne(queryWrapper);

        // 计算 BitMap 信息
        Long bitMap = CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType());
        if ((bitMap & couponTemplateRemindDO.getInformation()) == 0L) {
            throw new ClientException("您没有预约该时间点下的提醒");
        }

        // 计算 BitMap 信息
        bitMap ^= couponTemplateRemindDO.getInformation();
        queryWrapper.eq(CouponTemplateRemindDO::getInformation, couponTemplateRemindDO.getInformation());
        if (bitMap.equals(0L)) {
            // 如果新 BitMap 信息是 0，说明已经没有预约提醒了，可以直接删除
            if (couponTemplateRemindMapper.delete(queryWrapper) == 0) {
                // MySQL 乐观锁进行删除，如果删除失败，说明用户可能同时正在进行删除、新增提醒操作
                throw new ClientException("取消提醒失败，请刷新页面后重试");
            }
        } else {
            // 虽然删除了这个预约提醒，但还有其它提醒，那就更新数据库
            couponTemplateRemindDO.setInformation(bitMap);
            if (couponTemplateRemindMapper.update(couponTemplateRemindDO, queryWrapper) == 0) {
                // MySQL 乐观锁进行更新，如果更新失败，说明用户可能同时正在进行删除、新增提醒操作
                throw new ClientException("取消提醒失败，请刷新页面后重试");
            }
        }

        // 取消提醒这个信息添加到布隆过滤器中
        cancelRemindBloomFilter.add(String.valueOf(Objects.hash(requestParam.getCouponTemplateId(), requestParam.getUserId(), requestParam.getRemindTime(), requestParam.getType())));
        return true;
    }

    @Override
    public boolean isCancelRemind(RemindCouponTemplateDTO requestParam) {
        if (!cancelRemindBloomFilter.contains(String.valueOf(Objects.hash(requestParam.getCouponTemplateId(), requestParam.getUserId(), requestParam.getRemindTime(), requestParam.getType())))) {
            // 布隆过滤器中不存在，说明没取消提醒，此时已经能挡下大部分请求
            return false;
        }

        // 对于少部分的“取消了预约”，可能是误判，此时需要去数据库中查找
        LambdaQueryWrapper<CouponTemplateRemindDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateRemindDO.class)
                .eq(CouponTemplateRemindDO::getUserId, requestParam.getUserId())
                .eq(CouponTemplateRemindDO::getCouponTemplateId, requestParam.getCouponTemplateId());
        CouponTemplateRemindDO couponTemplateRemindDO = couponTemplateRemindMapper.selectOne(queryWrapper);
        if (null == couponTemplateRemindDO) {
            // 数据库中没该条预约提醒，说明被取消
            return true;
        }
        // 即使存在数据，也要检查该类型的该时间点是否有提醒
        Long information = couponTemplateRemindDO.getInformation();
        Long bitMap = CouponTemplateRemindUtil.calculateBitMap(requestParam.getRemindTime(), requestParam.getType());
        // 按位与等于 0 说明用户取消了预约
        return (bitMap & information) == 0L;
    }
}




