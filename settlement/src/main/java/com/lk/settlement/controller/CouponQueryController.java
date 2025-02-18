package com.lk.settlement.controller;

import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.settlement.dto.req.QueryCouponsReqDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;
import com.lk.settlement.service.CouponQueryService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 查询用户优惠券控制层
 *
 * @Author : lk
 * @create 2024/9/10
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "查询用户优惠券管理")
public class CouponQueryController {

    private final CouponQueryService couponQueryService;

    @Operation(summary = "查询用户可用/不可用优惠券列表")
    @PostMapping("/api/settlement/coupon-query")
    public Result<QueryCouponsRespDTO> listQueryCoupons(@RequestBody QueryCouponsReqDTO requestParam) {
        return Results.success(couponQueryService.listQueryUserCoupons(requestParam));
    }
}
