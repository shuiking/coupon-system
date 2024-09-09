package com.lk.engine.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询抢券预约提醒接口请求参数实体
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Data
@Schema(description = "查询优惠券预约抢券提醒参数实体")
public class CouponTemplateRemindQueryReqDTO {

    /**
     * 用户id
     */
    @Schema(description = "用户id", example = "1810868149847928832", required = true)
    private String userId;
}
