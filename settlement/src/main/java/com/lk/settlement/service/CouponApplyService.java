package com.lk.settlement.service;

import com.lk.settlement.dto.req.ApplyCouponReqDTO;
import com.lk.settlement.dto.resp.ApplyCouponRespDTO;

/**
 * 处理用户选择优惠券并应用折扣的服务
 *
 * @Author : lk
 * @create 2024/9/10
 */

public interface CouponApplyService {

    /**
     * 计算订单折扣后金额
     *
     * @param applyCouponReqDTO 请求参数
     * @param selectedCouponId  用户优惠券 ID
     * @return 订单折扣后金额
     */
    ApplyCouponRespDTO applySelectedCoupon(ApplyCouponReqDTO applyCouponReqDTO, Long selectedCouponId);
}
