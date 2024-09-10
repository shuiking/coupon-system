package com.lk.settlement.service.strategy;

import com.lk.settlement.dao.entity.CouponTemplateDO;
import com.lk.settlement.dao.entity.DiscountCouponDO;

import java.math.BigDecimal;

/**
 * 计算打折类型优惠券的优惠金额策略
 * 例如，如果折扣率为 0.8 且订单金额为 100，折扣后金额将为 80。
 *
 * @Author : lk
 * @create 2024/9/10
 */

public class DiscountCalculationStrategy implements CouponCalculationStrategy {

    @Override
    public BigDecimal calculateDiscount(CouponTemplateDO template, BigDecimal orderAmount) {
        DiscountCouponDO discountCoupon = (DiscountCouponDO) template;
        return orderAmount.multiply(BigDecimal.valueOf(discountCoupon.getDiscountRate()));
    }
}
