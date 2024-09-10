package com.lk.settlement.controller;

import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.settlement.dto.req.ApplyCouponReqDTO;
import com.lk.settlement.dto.resp.ApplyCouponRespDTO;
import com.lk.settlement.service.CouponApplyService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用用户优惠券控制层
 *
 * @Author : lk
 * @create 2024/9/10
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券结算管理")
public class CouponApplyController {

    private final CouponApplyService couponApplyService;

    @Operation(summary = "应用优惠券折扣订单金额")
    @PostMapping("/api/settlement/apply-coupon/{couponId}")
    public Result<ApplyCouponRespDTO> applySelectedCoupon(@RequestBody ApplyCouponReqDTO applyCouponReqDTO,
                                                          @PathVariable Long couponId) {
        ApplyCouponRespDTO response = couponApplyService.applySelectedCoupon(applyCouponReqDTO, couponId);
        return Results.success(response);
    }
}
