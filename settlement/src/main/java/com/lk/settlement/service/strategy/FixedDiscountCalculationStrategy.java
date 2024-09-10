package com.lk.settlement.service.strategy;

import com.lk.settlement.dao.entity.CouponTemplateDO;
import com.lk.settlement.dao.entity.FixedDiscountCouponDO;

import java.math.BigDecimal;

/**
 * 计算固定金额类型优惠券的优惠金额策略
 * 例如，如果优惠券的折扣金额为 50 元，使用该优惠券后，订单金额都会减少 50 元。
 *
 * @Author : lk
 * @create 2024/9/10
 */

public class FixedDiscountCalculationStrategy implements CouponCalculationStrategy {

    @Override
    public BigDecimal calculateDiscount(CouponTemplateDO template, BigDecimal orderAmount) {
        FixedDiscountCouponDO fixedDiscount = (FixedDiscountCouponDO) template;
        return BigDecimal.valueOf(fixedDiscount.getDiscountAmount());
    }
}
