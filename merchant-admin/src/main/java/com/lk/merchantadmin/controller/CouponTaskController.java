package com.lk.merchantadmin.controller;

import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.merchantadmin.dto.req.CouponTaskCreateReqDTO;
import com.lk.merchantadmin.service.CouponTaskService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券推送任务控制层
 *
 * @Author : lk
 * @create 2024/8/11
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券推送任务管理")
public class CouponTaskController {
    private final CouponTaskService couponTaskService;

    @Operation(summary = "商家创建优惠券推送任务")
    @PostMapping("/api/merchant-admin/coupon-task/create")
    public Result<Void> createCouponTask(@RequestBody CouponTaskCreateReqDTO requestParam) {
        couponTaskService.createCouponTask(requestParam);
        return Results.success();
    }


    @Operation(summary = "商家")
    @PostMapping("/1")
    public Result<Void> createCouponTask1(@RequestBody CouponTaskCreateReqDTO requestParam) {
        couponTaskService.createCouponTask(requestParam);
        return Results.success();
    }
}
