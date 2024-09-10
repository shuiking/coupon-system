package com.lk.settlement.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lk.settlement.common.constant.SettlementRedisConstant;
import com.lk.settlement.dao.entity.CouponTemplateDO;
import com.lk.settlement.dao.entity.UserCouponDO;
import com.lk.settlement.dao.mapper.CouponTemplateMapper;
import com.lk.settlement.dao.mapper.UserCouponMapper;
import com.lk.settlement.dto.req.QueryCouponsReqDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;
import com.lk.settlement.service.CouponCalculationService;
import com.lk.settlement.service.CouponQueryService;
import com.lk.settlement.toolkit.CouponFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * 查询用户可用优惠券列表接口
 *
 * @Author : lk
 * @create 2024/9/10
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponQueryServiceImpl implements CouponQueryService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserCouponMapper userCouponMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final CouponCalculationService couponCalculationService;

    @Override
    public CompletableFuture<List<QueryCouponsRespDTO>> queryUserCoupons(QueryCouponsReqDTO requestParam) {
        return CompletableFuture.supplyAsync(() -> {
            // 定义 Redis 操作对象
            ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

            // 构造缓存键
            String cacheKey = String.format(SettlementRedisConstant.COUPON_CACHE_KEY, requestParam.getUserId(), requestParam.getShopNumber());

            List<QueryCouponsRespDTO> cachedCoupons = null;

            // 尝试从缓存获取所有优惠券（获取的是JSON字符串）
            String cachedJson = valueOps.get(cacheKey);

            // 如果缓存命中，直接返回
            if (cachedJson != null) {
                // 将 JSON 字符串反序列化为 List<QueryCouponsRespDTO>
                cachedCoupons = JSON.parseArray(cachedJson, QueryCouponsRespDTO.class);
                return cachedCoupons;
            }

            // 查询用户所有优惠券
            List<UserCouponDO> allCoupons = queryAllUserCoupons(requestParam);

            // 收集所有的 couponTemplateId
            Set<Long> templateIds = allCoupons.stream().map(UserCouponDO::getCouponTemplateId).collect(Collectors.toSet());

            // 一次性查询所有优惠券模型的信息，降低DB压力
            List<CouponTemplateDO> templates = couponTemplateMapper.selectBatchIds(templateIds);

            // 将券模板信息存入Map，便于后续查找
            Map<Long, CouponTemplateDO> templateMap = templates.stream().collect(Collectors.toMap(CouponTemplateDO::getId, template -> template));

            // 筛选可用优惠券
            List<QueryCouponsRespDTO> availableCoupons = new ArrayList<>();

            allCoupons.forEach(coupon -> {
                CouponTemplateDO couponTemplate = templateMap.get(coupon.getCouponTemplateId());
                if (couponTemplate != null && isCouponApplicable(couponTemplate, requestParam)) {
                    // 创建具体的优惠券实例
                    CouponTemplateDO couponInstance = CouponFactory.createCoupon(couponTemplate, new HashMap<>());

                    // 计算优惠金额
                    BigDecimal couponAmount = couponCalculationService.calculateDiscount(couponInstance, requestParam.getOrderAmount());

                    // 转换成响应DTO
                    QueryCouponsRespDTO queryCouponsRespDTO = convertToRespDTO(coupon, couponTemplate, couponAmount);
                    queryCouponsRespDTO.setCouponAmount(couponAmount);
                    availableCoupons.add(queryCouponsRespDTO);
                }
            });

            // 与业内标准一致，按最终优惠力度从大到小排序
            availableCoupons.sort((c1, c2) -> c2.getCouponAmount().compareTo(c1.getCouponAmount()));

            try {
                // 将 CouponsRespDTO 对象序列化为 JSON 字符串
                String responseJson = JSON.toJSONString(availableCoupons);
                valueOps.set(cacheKey, responseJson);
            } catch (Exception ex) {
                // 记录缓存存储时的异常信息
                log.warn("Error storing to Redis：{}", ex.getMessage());
            }

            return availableCoupons;
        });
    }

    /**
     * 判断优惠券是否适用于当前订单：店铺匹配或全店通用，且订单金额满足消费规则
     *
     * @param template     优惠券模板
     * @param requestParam 查询用户优惠券参数
     * @return 优惠券是否适用当前订单
     */
    private boolean isCouponApplicable(CouponTemplateDO template, QueryCouponsReqDTO requestParam) {
        return template.getShopNumber().equals(requestParam.getShopNumber()) || template.getTarget() == 1;
    }

    /**
     * 查询用户所有优惠券
     *
     * @param requestParam 查询参数
     * @return 用户优惠券的结果列表
     */
    private List<UserCouponDO> queryAllUserCoupons(QueryCouponsReqDTO requestParam) {
        // 创建查询条件
        QueryWrapper<UserCouponDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", requestParam.getUserId()).orderByDesc("id");

        // 执行查询
        return userCouponMapper.selectList(queryWrapper);
    }

    /**
     * 转换 UserCouponDO 对象为 QueryCouponsRespDTO
     *
     * @param userCoupon 用户优惠券对象
     * @return 响应DTO
     */
    private QueryCouponsRespDTO convertToRespDTO(UserCouponDO userCoupon, CouponTemplateDO template, BigDecimal couponAmount) {
        return QueryCouponsRespDTO.builder()
                .couponTemplateId(template.getId())
                .couponName(template.getName())
                .couponAmount(couponAmount)
                .applicableGoods(template.getGoods())
                .applicableShop(template.getShopNumber().toString())
                .receiveTime(userCoupon.getReceiveTime())
                .validStartTime(userCoupon.getValidStartTime())
                .validEndTime(userCoupon.getValidEndTime())
                .status(userCoupon.getStatus())
                .build();
    }
}
