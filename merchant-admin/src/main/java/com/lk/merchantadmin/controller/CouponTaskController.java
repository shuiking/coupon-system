package com.lk.merchantadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.merchantadmin.dto.req.CouponTaskCreateReqDTO;
import com.lk.merchantadmin.dto.req.CouponTaskPageQueryReqDTO;
import com.lk.merchantadmin.dto.resp.CouponTaskPageQueryRespDTO;
import com.lk.merchantadmin.dto.resp.CouponTaskQueryRespDTO;
import com.lk.merchantadmin.service.CouponTaskService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "查询优惠券推送任务详情")
    @GetMapping("/api/merchant-admin/coupon-task/find")
    public Result<CouponTaskQueryRespDTO> findCouponTaskById(String taskId) {
        return Results.success(couponTaskService.findCouponTaskById(taskId));
    }

    @Operation(summary = "分页查询优惠券推送任务")
    @GetMapping("/api/merchant-admin/coupon-task/page")
    public Result<IPage<CouponTaskPageQueryRespDTO>> pageQueryCouponTask(@RequestBody CouponTaskPageQueryReqDTO requestParam) {
        return Results.success(couponTaskService.pageQueryCouponTask(requestParam));
    }
}
