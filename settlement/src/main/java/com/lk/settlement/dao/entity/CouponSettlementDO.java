package com.lk.settlement.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 优惠券结算单表
 *
 * @TableName t_coupon_settlement
 */

@TableName(value = "t_coupon_settlement")
@Data
public class CouponSettlementDO implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 店铺编号
     */
    private String shopNumber;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 结算单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 应付金额
     */
    private BigDecimal payableAmount;

    /**
     * 券金额
     */
    private BigDecimal couponAmount;

    /**
     * 结算单状态
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}