package com.lk.distribution.mq.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.lk.distribution.common.constant.DistributionRocketMQConstant;
import com.lk.distribution.common.enums.CouponTaskStatusEnum;
import com.lk.distribution.common.enums.CouponTemplateStatusEnum;
import com.lk.distribution.dao.entity.CouponTaskDO;
import com.lk.distribution.dao.mapper.CouponTaskMapper;
import com.lk.distribution.mq.producer.CouponTemplateExecuteProducer;
import com.lk.distribution.remote.CouponTemplateRemoteService;
import com.lk.distribution.remote.resp.CouponTemplateQueryRemoteRespDTO;
import com.lk.distribution.mq.base.MessageWrapper;
import com.lk.distribution.mq.event.CouponTaskExecuteEvent;
import com.lk.distribution.service.handler.excel.CouponTaskExcelObject;
import com.lk.distribution.service.handler.excel.ReadExcelDistributionListener;
import com.lk.framework.exception.RemoteException;
import com.lk.framework.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 优惠券推送定时执行-真实执行消费者
 *
 * @Author : lk
 * @create 2024/9/1
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = DistributionRocketMQConstant.TEMPLATE_TASK_EXECUTE_TOPIC_KEY,
        consumerGroup = DistributionRocketMQConstant.TEMPLATE_TASK_EXECUTE_CG_KEY
)

@Slf4j(topic = "CouponTaskExecuteConsumer")
public class CouponTaskExecuteConsumer implements RocketMQListener<MessageWrapper<CouponTaskExecuteEvent>> {

    private final CouponTaskMapper couponTaskMapper;
    private final CouponTemplateRemoteService couponTemplateRemoteService;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponTemplateExecuteProducer couponTemplateExecuteProducer;

    @Override
    public void onMessage(MessageWrapper<CouponTaskExecuteEvent> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券推送任务正式执行 - 执行消费逻辑，消息体：{}", JSON.toJSONString(messageWrapper));

        Long couponTaskId = messageWrapper.getMessage().getCouponTaskId();
        CouponTaskDO couponTaskDO = couponTaskMapper.selectById(couponTaskId);

        // 判断优惠券模板发送状态是否为执行中，如果不是有可能是被取消状态
        if (ObjectUtil.notEqual(couponTaskDO.getStatus(), CouponTaskStatusEnum.IN_PROGRESS.getStatus())) {
            log.warn("[消费者] 优惠券推送任务正式执行 - 推送任务记录状态异常：{}，已终止推送", couponTaskDO.getStatus());
            return;
        }


        Result<CouponTemplateQueryRemoteRespDTO> remoteCouponTemplateResult;
        try {
            // 调用引擎服务获取优惠券模板信息
            remoteCouponTemplateResult = couponTemplateRemoteService.pageQueryCouponTemplate(
                    String.valueOf(couponTaskDO.getShopNumber()),
                    String.valueOf(couponTaskDO.getCouponTemplateId())
            );

            // 判断远程调用优惠券模板信息数据是否为空或调用失败
            if (remoteCouponTemplateResult == null) {
                log.warn("[消费者] 优惠券推送任务正式执行 - 调用引擎服务层失败，将会重试调用");
                throw new RemoteException("调用引擎服务层优惠券模板返回空");
            } else if (remoteCouponTemplateResult.isFail()) {
                log.error("[消费者] 优惠券推送任务正式执行 - 调用引擎服务层失败，返回错误信息：{}", remoteCouponTemplateResult.getMessage());
                return;
            }
        } catch (Throwable ex) {
            log.warn("[消费者] 优惠券推送任务正式执行 - 调用引擎服务层失败，将会重试调用", ex);
            throw ex;
        }

        CouponTemplateQueryRemoteRespDTO actualRemoteCouponTemplate = remoteCouponTemplateResult.getData();
        Integer status = actualRemoteCouponTemplate.getStatus();
        if (ObjectUtil.notEqual(status, CouponTemplateStatusEnum.ACTIVE.getStatus())) {
            log.error("[消费者] 优惠券推送任务正式执行 - 优惠券ID：{}，优惠券模板状态：{}", actualRemoteCouponTemplate.getId(), status);
            return;
        }

        // 正式开始执行优惠券推送任务
        ReadExcelDistributionListener readExcelDistributionListener = new ReadExcelDistributionListener(
                couponTaskDO,
                actualRemoteCouponTemplate,
                stringRedisTemplate,
                couponTemplateExecuteProducer
        );
        EasyExcel.read(couponTaskDO.getFileAddress(), CouponTaskExcelObject.class, readExcelDistributionListener).sheet().doRead();
    }
}
