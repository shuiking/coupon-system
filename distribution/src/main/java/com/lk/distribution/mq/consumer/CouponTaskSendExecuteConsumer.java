package com.lk.distribution.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.lk.distribution.common.constant.MerchantAdminRocketMQConstant;
import com.lk.distribution.mq.base.MessageWrapper;
import com.lk.distribution.mq.event.CouponTaskDelayEvent;
import com.lk.distribution.mq.event.CouponTaskExecuteEvent;
import com.lk.distribution.mq.producer.CouponTaskActualExecuteProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 优惠券推送定时执行-发送执行命令消费者
 *
 * @Author : lk
 * @create 2024/9/2
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MerchantAdminRocketMQConstant.TEMPLATE_TASK_DELAY_TOPIC_KEY,
        consumerGroup = MerchantAdminRocketMQConstant.TEMPLATE_TASK_SEND_EXECUTE_CG_KEY
)
@Slf4j(topic = "CouponTaskSendExecuteConsumer")
public class CouponTaskSendExecuteConsumer implements RocketMQListener<MessageWrapper<CouponTaskDelayEvent>> {

    private final CouponTaskActualExecuteProducer couponTaskActualExecuteProducer;

    @Override
    public void onMessage(MessageWrapper<CouponTaskDelayEvent> couponTaskDelayEventMessageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券推送定时执行@发送执行命令 - 执行消费逻辑，消息体：{}", JSON.toJSONString(couponTaskDelayEventMessageWrapper));

        CouponTaskExecuteEvent couponTaskExecuteEvent = CouponTaskExecuteEvent.builder()
                .couponTaskId(couponTaskDelayEventMessageWrapper.getMessage().getCouponTaskId())
                .build();

        couponTaskActualExecuteProducer.sendMessage(couponTaskExecuteEvent);
    }
}
