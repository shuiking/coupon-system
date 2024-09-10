package com.lk.settlement.service.impl;

import com.lk.settlement.dto.req.ApplyCouponReqDTO;
import com.lk.settlement.dto.resp.ApplyCouponRespDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;
import com.lk.settlement.service.CouponApplyService;
import com.lk.settlement.service.CouponQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 优惠券应用服务实现类
 *
 * @Author : lk
 * @create 2024/9/10
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponApplyServiceImpl implements CouponApplyService {

    private final CouponQueryService couponQueryService;

    @Override
    public ApplyCouponRespDTO applySelectedCoupon(ApplyCouponReqDTO applyCouponReqDTO, Long selectedCouponId) {
        // 获取用户所有可用优惠券
        List<QueryCouponsRespDTO> availableCoupons = couponQueryService.queryUserCoupons(applyCouponReqDTO.toQueryCouponsReqDTO()).join();

        // 查找用户选择的优惠券
        Optional<QueryCouponsRespDTO> selectedCouponOpt = availableCoupons.stream()
                .filter(coupon -> coupon.getCouponTemplateId().equals(selectedCouponId))
                .findFirst();

        if (selectedCouponOpt.isPresent()) {
            QueryCouponsRespDTO selectedCoupon = selectedCouponOpt.get();

            // 获取优惠金额
            BigDecimal discountAmount = selectedCoupon.getCouponAmount();
            // 计算折后金额
            BigDecimal finalAmount = applyCouponReqDTO.getOrderAmount().subtract(discountAmount);

            return ApplyCouponRespDTO.builder()
                    .orderId(applyCouponReqDTO.getOrderId())
                    .originalAmount(applyCouponReqDTO.getOrderAmount())
                    .finalAmount(finalAmount)
                    .appliedCouponId(selectedCoupon.getCouponTemplateId())
                    .build();
        } else {
            // 未找到匹配的优惠券，返回原金额
            return ApplyCouponRespDTO.builder()
                    .orderId(applyCouponReqDTO.getOrderId())
                    .originalAmount(applyCouponReqDTO.getOrderAmount())
                    .finalAmount(applyCouponReqDTO.getOrderAmount())
                    .appliedCouponId(null)
                    .build();
        }
    }
}
