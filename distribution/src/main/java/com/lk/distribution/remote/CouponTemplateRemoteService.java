package com.lk.distribution.remote;

import com.lk.distribution.remote.resp.CouponTemplateQueryRemoteRespDTO;
import com.lk.framework.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 引擎服务优惠券模板远程调用
 *
 * @Author : lk
 * @create 2024/9/1
 */

@FeignClient(value = "engine${unique-name:}", url = "${coupon.distribution.feign.remote-url.engine:}")
public interface CouponTemplateRemoteService {

    /**
     * 查询优惠券模板
     */
    @GetMapping("/api/engine/coupon-template/query")
    Result<CouponTemplateQueryRemoteRespDTO> pageQueryCouponTemplate(
            @RequestParam(value = "shopNumber") String shopNumber,
            @RequestParam(value = "couponTemplateId") String couponTemplateId
    );
}
