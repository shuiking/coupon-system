package com.lk.engine.controller;

import com.lk.engine.dto.req.CouponTemplateRedeemReqDTO;
import com.lk.engine.service.UserCouponService;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户优惠券控制层
 *
 * @Author : lk
 * @create 2024/9/9
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "用户优惠券管理")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @Operation(summary = "兑换优惠券模板", description = "存在较高流量场景，可类比“秒杀”业务")
    @PostMapping("/api/engine/user-coupon/redeem")
    public Result<Void> redeemUserCoupon(@RequestBody CouponTemplateRedeemReqDTO requestParam) {
        userCouponService.redeemUserCoupon(requestParam);
        return Results.success();
    }
}
