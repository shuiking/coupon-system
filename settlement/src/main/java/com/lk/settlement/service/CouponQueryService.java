package com.lk.settlement.service;

import com.lk.settlement.dto.req.QueryCouponsReqDTO;
import com.lk.settlement.dto.resp.QueryCouponsRespDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 查询用户可用优惠券列表接口
 *
 * @Author : lk
 * @create 2024/9/10
 */

public interface CouponQueryService {

    /**
     * 查询用户可用的优惠券列表，返回 CouponsRespDTO 对象
     *
     * @param requestParam 查询参数
     * @return CompletableFuture<CouponsRespDTO> 包含可用优惠券的分页结果
     */
    CompletableFuture<List<QueryCouponsRespDTO>> queryUserCoupons(QueryCouponsReqDTO requestParam);
}
