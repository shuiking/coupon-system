package com.lk.settlement.service.strategy;

import com.lk.settlement.dao.entity.CouponTemplateDO;

import java.math.BigDecimal;

/**
 * 优惠券折扣金额的策略接口
 *
 * @Author : lk
 * @create 2024/9/10
 */

public interface CouponCalculationStrategy {

    /**
     * 计算折扣
     *
     * @param template    优惠券模板
     * @param orderAmount 订单金额
     * @return 优惠后金额
     */
    BigDecimal calculateDiscount(CouponTemplateDO template, BigDecimal orderAmount);
}
