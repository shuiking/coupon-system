package com.lk.distribution.remote.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模板查询接口请求参数实体
 *
 * @Author : lk
 * @create 2024/9/2
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateQueryRemoteReqDTO {

    /**
     * 店铺编号
     */
    private String shopNumber;

    /**
     * 优惠券模板id
     */
    private String couponTemplateId;
}
