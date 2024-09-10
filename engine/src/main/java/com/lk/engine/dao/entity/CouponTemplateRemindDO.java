package com.lk.engine.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户预约提醒信息存储表
 *
 * @TableName t_coupon_template_remind
 */

@TableName(value = "t_coupon_template_remind")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CouponTemplateRemindDO implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 券ID
     */
    private Long couponTemplateId;

    /**
     * 存储信息
     */
    private Long information;

    /**
     * 店铺编号
     */
    private Long shopNumber;

    /**
     * 优惠券开抢时间
     */
    private Date startTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}