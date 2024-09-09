package com.lk.engine.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 预约提醒方式枚举类，值必须是 0，1，2，3......
 *
 * @Author : lk
 * @create 2024/9/9
 */

@RequiredArgsConstructor
public enum CouponRemindTypeEnum {

    /**
     * 邮件提醒
     */
    EMAIL(0, "邮件提醒"),
    MESSAGE(1, "短信提醒");

    @Getter
    private final int type;
    @Getter
    private final String describe;

    public static CouponRemindTypeEnum getByType(Integer type) {
        for (CouponRemindTypeEnum remindEnum : values()) {
            if (remindEnum.getType() == type) {
                return remindEnum;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }

    public static String getDescribeByType(Integer type) {
        for (CouponRemindTypeEnum remindEnum : values()) {
            if (remindEnum.getType() == type) {
                return remindEnum.getDescribe();
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }
}
