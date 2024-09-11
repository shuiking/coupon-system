package com.lk.search.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.search.dto.req.CouponTemplatePageQueryReqDTO;
import com.lk.search.dto.resp.CouponTemplatePageQueryRespDTO;
import com.lk.search.service.CouponTemplateSearchService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券模板搜索控制层
 *
 * @Author : lk
 * @create 2024/9/11
 */

@RestController
@RequiredArgsConstructor
@Api(tags = "优惠券模板搜索管理")
public class CouponTemplateController {

    private final CouponTemplateSearchService couponTemplateSearchService;

    @Operation(summary = "分页查询优惠券模板")
    @GetMapping("/api/search/coupon-template/page")
    public Result<IPage<CouponTemplatePageQueryRespDTO>> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam) {
        return Results.success(couponTemplateSearchService.pageQueryCouponTemplate(requestParam));
    }
}
