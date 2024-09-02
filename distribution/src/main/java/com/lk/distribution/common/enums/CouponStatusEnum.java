package com.lk.distribution.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券使用状态枚举类
 *
 * @Author : lk
 * @create 2024/9/2
 */

@RequiredArgsConstructor
public enum CouponStatusEnum {

    /**
     * 生效中
     */
    EFFECTIVE(0),

    /**
     * 已结束
     */
    ENDED(1);

    @Getter
    private final int type;
}
