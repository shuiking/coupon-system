package com.lk.merchantadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lk.framework.idempotent.NoDuplicateSubmit;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.merchantadmin.dto.req.CouponTemplateNumberReqDTO;
import com.lk.merchantadmin.dto.req.CouponTemplatePageQueryReqDTO;
import com.lk.merchantadmin.dto.req.CouponTemplateSaveReqDTO;
import com.lk.merchantadmin.dto.resp.CouponTemplatePageQueryRespDTO;
import com.lk.merchantadmin.dto.resp.CouponTemplateQueryRespDTO;
import com.lk.merchantadmin.service.CouponTemplateService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券模板控制层
 *
 * @Author : lk
 * @create 2024/8/25
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券模板管理")
public class CouponTemplateController {
    private final CouponTemplateService couponTemplateService;

    @Operation(summary = "商家创建优惠券模板")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交优惠券模板")
    @PostMapping("/api/merchant-admin/coupon-template/create")
    public Result<Void> createCouponTemplate(@RequestBody CouponTemplateSaveReqDTO requestParam) {
        couponTemplateService.createCouponTemplate(requestParam);
        return Results.success();
    }

    @Operation(summary = "查询优惠券模板详情")
    @GetMapping("/api/merchant-admin/coupon-template/find")
    public Result<CouponTemplateQueryRespDTO> findCouponTemplate(String couponTemplateId) {
        return Results.success(couponTemplateService.findCouponTemplateById(couponTemplateId));
    }

    @Operation(summary = "增加优惠券模板发行量")
    @NoDuplicateSubmit(message = "请勿短时间内重复增加优惠券发行量")
    @PostMapping("/api/merchant-admin/coupon-template/increase-number")
    public Result<Void> increaseNumberCouponTemplate(@RequestBody CouponTemplateNumberReqDTO requestParam) {
        couponTemplateService.increaseNumberCouponTemplate(requestParam);
        return Results.success();
    }

    @Operation(summary = "结束优惠券模板")
    @PostMapping("/api/merchant-admin/coupon-template/terminate")
    public Result<Void> terminateCouponTemplate(String couponTemplateId) {
        couponTemplateService.terminateCouponTemplate(couponTemplateId);
        return Results.success();
    }

    @Operation(summary = "分页查询优惠券模板")
    @GetMapping("/api/merchant-admin/coupon-template/page")
    public Result<IPage<CouponTemplatePageQueryRespDTO>> pageQueryCouponTemplate(@RequestBody CouponTemplatePageQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.pageQueryCouponTemplate(requestParam));
    }

}
