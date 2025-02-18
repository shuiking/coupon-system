package com.lk.merchantadmin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模板操作日志表
 *
 * @TableName t_coupon_template_log
 */
@TableName(value = "t_coupon_template_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTemplateLogDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 店铺编号
     */
    private Long shopNumber;

    /**
     * 优惠券模板ID
     */
    private Long couponTemplateId;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     * 操作日志
     */
    private String operationLog;

    /**
     * 原始数据
     */
    private String originalData;

    /**
     * 修改后数据
     */
    private String modifiedData;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}