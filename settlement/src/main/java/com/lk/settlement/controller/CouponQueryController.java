package com.lk.settlement.controller;

import com.lk.framework.result.Result;
import com.lk.settlement.dto.req.QueryCouponsReqDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;
import com.lk.settlement.handler.AsyncResponseHandler;
import com.lk.settlement.service.CouponQueryService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private final AsyncResponseHandler asyncResponseHandler;

    @Operation(summary = "查询用户可用的优惠券列表")
    @GetMapping("/api/settlement/coupon-query")
    public DeferredResult<Result<List<QueryCouponsRespDTO>>> pageQueryAvailableCoupons(QueryCouponsReqDTO requestParam) {
        // 调用服务方法，获取异步结果
        CompletableFuture<List<QueryCouponsRespDTO>> couponsFuture = couponQueryService.queryUserCoupons(requestParam);

        // 使用异步响应处理器返回结果
        return asyncResponseHandler.createDeferredResult(couponsFuture);
    }
}
