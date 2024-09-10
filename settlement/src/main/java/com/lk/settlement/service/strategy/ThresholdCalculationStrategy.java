package com.lk.settlement.service.strategy;

import com.lk.settlement.dao.entity.CouponTemplateDO;
import com.lk.settlement.dao.entity.ThresholdCouponDO;

import java.math.BigDecimal;

/**
 * 计算有门槛固定金额类型优惠券的优惠金额策略
 * 例如，如果优惠券的折扣金额为 50 元，门槛为100 元，满足门槛的订单 使用该优惠券后，订单金额会减少 50 元。
 *
 * @Author : lk
 * @create 2024/9/10
 */

public class ThresholdCalculationStrategy implements CouponCalculationStrategy {

    @Override
    public BigDecimal calculateDiscount(CouponTemplateDO template, BigDecimal orderAmount) {
        ThresholdCouponDO thresholdDiscount = (ThresholdCouponDO) template;
        if (orderAmount.compareTo(BigDecimal.valueOf(thresholdDiscount.getThresholdAmount())) >= 0) {
            return BigDecimal.valueOf(thresholdDiscount.getDiscountAmount());
        }
        return BigDecimal.ZERO;
    }
}
