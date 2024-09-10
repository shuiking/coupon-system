package com.lk.settlement.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询用户优惠券请求参数
 *
 * @Author : lk
 * @create 2024/9/10
 */

@Data
public class QueryCouponsReqDTO {

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "订单金额", required = true)
    private BigDecimal orderAmount;

    @Schema(description = "店铺编号", required = true)
    private Long shopNumber;

    @Schema(description = "最大返回条数", required = true)
    private Integer limit = 50;
}
