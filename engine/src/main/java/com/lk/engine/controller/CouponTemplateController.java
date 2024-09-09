package com.lk.engine.controller;

import com.lk.engine.dto.req.CouponTemplateQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.lk.engine.service.CouponTemplateService;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠卷模版控制层
 *
 * @Author : lk
 * @create 2024/9/6
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券模板管理")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @Operation(summary = "查询优惠券模板")
    @GetMapping("/api/engine/coupon-template/query")
    public Result<CouponTemplateQueryRespDTO> findCouponTemplate(CouponTemplateQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.findCouponTemplate(requestParam));
    }
}
