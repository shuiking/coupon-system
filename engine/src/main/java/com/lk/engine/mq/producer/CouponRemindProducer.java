package com.lk.engine.mq.producer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.lk.engine.common.constant.EngineRockerMQConstant;
import com.lk.engine.mq.base.BaseSendExtendDTO;
import com.lk.engine.mq.base.MessageWrapper;
import com.lk.engine.mq.event.CouponRemindEvent;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 提醒抢券生产者
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Component
public class CouponRemindProducer extends AbstractCommonSendProduceTemplate<CouponRemindEvent> {

    private final ConfigurableEnvironment environment;

    public CouponRemindProducer(@Autowired RocketMQTemplate rocketMQTemplate, @Autowired ConfigurableEnvironment environment) {
        super(rocketMQTemplate);
        this.environment = environment;
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(CouponRemindEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("提醒用户抢券")
                .keys(messageSendEvent.getUserId() + ":" + messageSendEvent.getCouponTemplateId())
                .topic(environment.resolvePlaceholders(EngineRockerMQConstant.COUPON_TEMPLATE_REMIND_TOPIC_KEY))
                .sentTimeout(2000L)
                .delayTime(DateUtil.offsetMinute(messageSendEvent.getStartTime(), -messageSendEvent.getRemindTime()).getTime())
                .build();
    }

    @Override
    protected Message<?> buildMessage(CouponRemindEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(requestParam.getKeys(), messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
