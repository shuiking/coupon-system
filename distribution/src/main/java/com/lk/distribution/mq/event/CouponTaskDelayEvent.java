package com.lk.distribution.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券推送任务定时执行事件
 *
 * @Author : lk
 * @create 2024/9/2
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponTaskDelayEvent {

    /**
     * 推送任务id
     */
    private Long couponTaskId;

    /**
     * 发送状态
     */
    private Integer status;
}
