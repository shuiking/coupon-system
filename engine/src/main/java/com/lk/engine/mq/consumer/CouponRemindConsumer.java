package com.lk.engine.mq.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.lk.engine.common.constant.EngineRockerMQConstant;
import com.lk.engine.mq.base.MessageWrapper;
import com.lk.engine.mq.event.CouponRemindEvent;
import com.lk.engine.service.handle.remind.ExecuteRemindCouponTemplate;
import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 提醒抢券消费者
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = EngineRockerMQConstant.COUPON_TEMPLATE_REMIND_TOPIC_KEY,
        consumerGroup = EngineRockerMQConstant.COUPON_TEMPLATE_REMIND_CG_KEY
)
@Slf4j(topic = "CouponRemindConsumer")
public class CouponRemindConsumer implements RocketMQListener<MessageWrapper<CouponRemindEvent>> {

    private final ExecuteRemindCouponTemplate executeRemindCouponTemplate;

    @Override
    public void onMessage(MessageWrapper<CouponRemindEvent> messageWrapper) {
        log.info("[消费者] 提醒用户抢券 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));
        CouponRemindEvent event = messageWrapper.getMessage();
        RemindCouponTemplateDTO remindCouponTemplateDTO = BeanUtil.toBean(event, RemindCouponTemplateDTO.class);
        // 根据不同策略向用户发送消息提醒
        executeRemindCouponTemplate.executeRemindCouponTemplate(remindCouponTemplateDTO);
    }
}
