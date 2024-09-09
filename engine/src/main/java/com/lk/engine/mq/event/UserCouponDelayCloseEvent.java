package com.lk.engine.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户优惠券延时关闭事件
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCouponDelayCloseEvent {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户优惠券id
     */
    private String userCouponId;

    /**
     * 优惠券模板id
     */
    private String couponTemplateId;

    /**
     * 具体延迟时间
     */
    private Long delayTime;
}
