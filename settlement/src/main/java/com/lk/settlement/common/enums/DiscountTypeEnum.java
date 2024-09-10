package com.lk.settlement.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券优惠类型
 *
 * @Author : lk
 * @create 2024/9/10
 */

@RequiredArgsConstructor
public enum DiscountTypeEnum {

    /**
     * 立减券
     */
    FIXED_DISCOUNT(0, "立减券"),

    /**
     * 满减券
     */
    THRESHOLD_DISCOUNT(1, "满减券"),

    /**
     * 折扣券
     */
    DISCOUNT_COUPON(2, "折扣券");

    @Getter
    private final int type;

    @Getter
    private final String value;

    /**
     * 根据 type 找到对应的枚举
     *
     * @param type 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static DiscountTypeEnum getByType(int type) {
        for (DiscountTypeEnum discountEnum : values()) {
            if (discountEnum.getType() == type) {
                return discountEnum;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }
}
