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
 * 用户优惠券表
 *
 * @TableName t_user_coupon
 */

@TableName(value = "t_user_coupon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserCouponDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券模板ID
     */
    private Long couponTemplateId;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 领取次数
     */
    private Integer receiveCount;

    /**
     * 有效期开始时间
     */
    private Date validStartTime;

    /**
     * 有效期结束时间
     */
    private Date validEndTime;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 券来源 0：领券中心 1：平台发放 2：店铺领取
     */
    private Integer source;

    /**
     * 状态 0：未使用 1：锁定 2：已使用 3：已过期 4：已撤回
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}