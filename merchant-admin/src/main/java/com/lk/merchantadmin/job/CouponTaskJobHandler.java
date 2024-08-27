package com.lk.merchantadmin.job;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lk.framework.result.Result;
import com.lk.framework.web.Results;
import com.lk.merchantadmin.dao.entity.CouponTaskDO;
import com.lk.merchantadmin.dao.mapper.CouponTaskMapper;
import com.lk.merchantadmin.enums.CouponTaskStatusEnum;
import com.lk.merchantadmin.mq.event.CouponTaskDelayEvent;
import com.lk.merchantadmin.mq.producer.CouponTaskDelayExecuteProducer;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 优惠券推送任务扫描定时发送记录 XXL-Job 处理器
 *
 * @Author : lk
 * @create 2024/8/27
 */

@Component
@RequiredArgsConstructor
@RestController
@Api(tags = "优惠券定时推送任务")
public class CouponTaskJobHandler {

    private final CouponTaskMapper couponTaskMapper;
    private final CouponTaskDelayExecuteProducer couponTaskDelayExecuteProducer;
    private static final int MAX_LIMIT = 100;

    @SneakyThrows
    @Operation(summary = "执行优惠券定时推送")
    @GetMapping("/api/merchant-admin/other/coupon-task/job")
    public Result<Void> webExecute() {
        execute();
        return Results.success();
    }

    @XxlJob(value = "couponTemplateTask")
    public void execute() {
        long initId = 0;
        Date now = new Date();

        while (true) {
            List<CouponTaskDO> couponTaskDOList = fetchPendingTasks(initId, now);

            if (CollUtil.isEmpty(couponTaskDOList)) {
                break;
            }

            // 调用分发服务对用户发送优惠券
            for (CouponTaskDO each : couponTaskDOList) {
                distributeCoupon(each);
            }

            if (couponTaskDOList.size() < MAX_LIMIT) {
                break;
            }

            // 更新 initId 为当前列表中最大 ID
            initId = couponTaskDOList.stream()
                    .mapToLong(CouponTaskDO::getId)
                    .max()
                    .orElse(initId);
        }
    }

    private void distributeCoupon(CouponTaskDO couponTask) {
        // 通过消息队列发送消息，修改状态记录并由分发服务消费者消费该消息
        CouponTaskDelayEvent couponTaskDelayEvent = CouponTaskDelayEvent.builder()
                .couponTaskId(couponTask.getId())
                .status(CouponTaskStatusEnum.IN_PROGRESS.getStatus())
                .build();
        couponTaskDelayExecuteProducer.sendMessage(couponTaskDelayEvent);
    }

    private List<CouponTaskDO> fetchPendingTasks(long initId, Date now) {
        LambdaQueryWrapper<CouponTaskDO> queryWrapper = Wrappers.lambdaQuery(CouponTaskDO.class)
                .eq(CouponTaskDO::getStatus, CouponTaskStatusEnum.PENDING.getStatus())
                .le(CouponTaskDO::getSendTime, now)
                .gt(CouponTaskDO::getId, initId)
                .last("LIMIT " + MAX_LIMIT);
        return couponTaskMapper.selectList(queryWrapper);
    }
}
