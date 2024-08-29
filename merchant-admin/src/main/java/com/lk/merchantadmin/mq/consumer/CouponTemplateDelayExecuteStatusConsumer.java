package com.lk.merchantadmin.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lk.merchantadmin.common.constant.MerchantAdminRocketMQConstant;
import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.lk.merchantadmin.enums.CouponTemplateStatusEnum;
import com.lk.merchantadmin.mq.base.MessageWrapper;
import com.lk.merchantadmin.mq.event.CouponTemplateDelayEvent;
import com.lk.merchantadmin.service.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 优惠券模板推送延迟执行-变更记录状态消费者
 *
 * @Author : lk
 * @create 2024/8/29
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = MerchantAdminRocketMQConstant.TEMPLATE_TEMPLATE_DELAY_TOPIC_KEY, consumerGroup = MerchantAdminRocketMQConstant.TEMPLATE_TEMPLATE_DELAY_STATUS_CG_KEY)
@Slf4j(topic = "CouponTemplateDelayExecuteStatusConsumer")
public class CouponTemplateDelayExecuteStatusConsumer implements RocketMQListener<MessageWrapper<CouponTemplateDelayEvent>> {

    private final CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponTemplateDelayEvent> couponTemplateDelayEventMessageWrapper) {
        log.info("[消费者] 优惠券模板定时执行@变更模板表状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(couponTemplateDelayEventMessageWrapper));

        // 修改指定优惠券模板状态为已结束
        CouponTemplateDelayEvent message = couponTemplateDelayEventMessageWrapper.getMessage();
        LambdaUpdateWrapper<CouponTemplateDO> updateWrapper = Wrappers.lambdaUpdate(CouponTemplateDO.class).eq(CouponTemplateDO::getShopNumber, message.getShopNumber()).eq(CouponTemplateDO::getId, message.getCouponTemplateId()).set(CouponTemplateDO::getStatus, CouponTemplateStatusEnum.ENDED.getStatus());
        couponTemplateService.update(updateWrapper);
    }
}
