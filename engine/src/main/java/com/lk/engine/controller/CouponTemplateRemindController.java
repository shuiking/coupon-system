package com.lk.engine.controller;

import com.lk.engine.dto.req.CouponTemplateRemindCancelReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.lk.engine.service.CouponTemplateRemindService;
import com.lk.framework.idempotent.NoDuplicateSubmit;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 优惠券模板控制层
 *
 * @Author : lk
 * @create 2024/9/9
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券预约提醒管理")
public class CouponTemplateRemindController {

    private final CouponTemplateRemindService couponTemplateRemindService;

    @Operation(summary = "发出优惠券预约提醒请求")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交预约提醒请求")
    @PostMapping("/api/engine/coupon-template-remind/create")
    public Result<Boolean> createCouponRemind(@RequestBody CouponTemplateRemindCreateReqDTO requestParam) {
        return Results.success(couponTemplateRemindService.createCouponRemind(requestParam));
    }

    @Operation(summary = "查询优惠券预约提醒")
    @GetMapping("/api/engine/coupon-template-remind/list")
    public Result<List<CouponTemplateRemindQueryRespDTO>> listCouponRemind(CouponTemplateRemindQueryReqDTO requestParam) {
        return Results.success(couponTemplateRemindService.listCouponRemind(requestParam));
    }

    @Operation(summary = "取消优惠券预约提醒")
    @NoDuplicateSubmit(message = "请勿短时间内重复提交取消预约提醒请求")
    @PostMapping("/api/engine/coupon-template-remind/cancel")
    public Result<Boolean> cancelCouponRemind(@RequestBody CouponTemplateRemindCancelReqDTO requestParam) {
        return Results.success(couponTemplateRemindService.cancelCouponRemind(requestParam));
    }
}
