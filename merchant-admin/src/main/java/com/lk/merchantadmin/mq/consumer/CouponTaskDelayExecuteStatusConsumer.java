package com.lk.merchantadmin.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.lk.merchantadmin.common.constant.MerchantAdminRocketMQConstant;
import com.lk.merchantadmin.dao.entity.CouponTaskDO;
import com.lk.merchantadmin.mq.base.MessageWrapper;
import com.lk.merchantadmin.mq.event.CouponTaskDelayEvent;
import com.lk.merchantadmin.service.CouponTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author : lk
 * @create 2024/8/29
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MerchantAdminRocketMQConstant.TEMPLATE_TASK_DELAY_TOPIC_KEY,
        consumerGroup = MerchantAdminRocketMQConstant.TEMPLATE_TASK_DELAY_STATUS_CG_KEY
)
@Slf4j(topic = "CouponTaskDelayExecuteStatusConsumer")
public class CouponTaskDelayExecuteStatusConsumer implements RocketMQListener<MessageWrapper<CouponTaskDelayEvent>> {

    private final CouponTaskService couponTaskService;

    @Override
    public void onMessage(MessageWrapper<CouponTaskDelayEvent> couponTaskDelayEventMessageWrapper) {
        log.info("[消费者] 优惠券推送定时执行@变更记录发送状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(couponTaskDelayEventMessageWrapper));

        // 修改延时执行推送任务任务状态为执行中
        CouponTaskDelayEvent message = couponTaskDelayEventMessageWrapper.getMessage();
        CouponTaskDO couponTaskDO = CouponTaskDO.builder()
                .id(message.getCouponTaskId())
                .status(message.getStatus())
                .build();
        couponTaskService.updateById(couponTaskDO);
    }
}
