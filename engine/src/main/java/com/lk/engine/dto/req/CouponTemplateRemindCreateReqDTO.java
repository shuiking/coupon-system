package com.lk.engine.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 创建抢券预约提醒接口请求参数实体
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Data
@Schema(description = "优惠券预约抢券提醒请求参数实体")
public class CouponTemplateRemindCreateReqDTO {

    /**
     * 优惠券模板id
     */
    @Schema(description = "优惠券模板id", example = "1810966706881941507", required = true)
    private String couponTemplateId;

    /**
     * 优惠券名称
     */
    @Schema(description = "优惠券名称")
    private String name;

    /**
     * 店铺编号
     */
    @Schema(description = "店铺编号", example = "1810714735922956666", required = true)
    private String shopNumber;

    /**
     * 用户id
     */
    @Schema(description = "用户id", example = "1810868149847928832", required = true)
    private String userId;

    /**
     * 用户联系方式，可能是邮箱、手机号、等等
     */
    @Schema(description = "用户联系方式")
    private String contact;

    /**
     * 提醒方式
     */
    @Schema(description = "提醒方式", example = "0", required = true)
    private Integer type;

    /**
     * 提醒时间，比如五分钟，十分钟，十五分钟
     */
    @Schema(description = "提醒时间")
    private Integer remindTime;

    /**
     * 开抢时间
     */
    @Schema(description = "开抢时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
}
