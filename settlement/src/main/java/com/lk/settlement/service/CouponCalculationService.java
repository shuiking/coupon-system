package com.lk.settlement.service;

import com.lk.settlement.dao.entity.CouponTemplateDO;
import com.lk.settlement.service.strategy.CouponCalculationStrategy;
import com.lk.settlement.toolkit.CouponFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 根据不同的优惠券类型，动态选择相应的计算策略，并返回计算后的优惠金额。
 *
 * @Author : lk
 * @create 2024/9/10
 */

@Service
public class CouponCalculationService {

    /**
     * 计算优惠金额。
     * 根据传入的优惠券实例和订单金额，选择相应的计算策略，返回最终的优惠金额。
     *
     * @param coupon      具体的优惠券实例
     * @param orderAmount 订单金额
     * @return 计算出的优惠金额
     */
    public BigDecimal calculateDiscount(CouponTemplateDO coupon, BigDecimal orderAmount) {
        CouponCalculationStrategy strategy = CouponFactory.getCouponCalculationStrategy(coupon);
        return strategy.calculateDiscount(coupon, orderAmount);
    }
}
