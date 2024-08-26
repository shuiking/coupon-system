package com.lk.merchantadmin.controller;

import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : lk
 * @create 2024/8/25
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券模板管理")
public class CouponTemplateController {

    @Operation(summary = "商家创建优惠券模板")
    @PostMapping("/api/merchant-admin/coupon-template/create")
    public Result<Void> createCouponTemplate() {
        return Results.success();
    }
}
