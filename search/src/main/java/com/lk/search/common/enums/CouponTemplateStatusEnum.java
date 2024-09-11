package com.lk.search.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券模板状态枚举
 *
 * @Author : lk
 * @create 2024/9/11
 */

@RequiredArgsConstructor
public enum CouponTemplateStatusEnum {

    /**
     * 生效中
     */
    EFFECTIVE(0),

    /**
     * 已结束
     */
    EXPIRED(1),

    /**
     * 未删除
     */
    NORMAL(0),

    /**
     * 已删除
     */
    DELETED(1);

    @Getter
    private final Integer code;
}
