package com.lk.distribution.service.handler.excel;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lk.distribution.common.constant.DistributionRedisConstant;
import com.lk.distribution.common.constant.EngineRedisConstant;
import com.lk.distribution.dao.entity.CouponTaskDO;
import com.lk.distribution.mq.event.CouponTemplateExecuteEvent;
import com.lk.distribution.mq.producer.CouponTemplateExecuteProducer;
import com.lk.distribution.remote.resp.CouponTemplateQueryRemoteRespDTO;
import com.lk.distribution.toolkit.StockDecrementReturnCombinedUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * 优惠券任务读取 Excel 分发监听器
 *
 * @Author : lk
 * @create 2024/8/31
 */

@RequiredArgsConstructor
public class ReadExcelDistributionListener extends AnalysisEventListener<CouponTaskExcelObject> {

    private final CouponTaskDO couponTask;
    private final CouponTemplateQueryRemoteRespDTO couponTemplate;

    private final StringRedisTemplate stringRedisTemplate;
    private final CouponTemplateExecuteProducer couponTemplateExecuteProducer;

    @Getter
    private int rowCount = 0;
    private final static String STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH = "lua/stock_decrement_and_batch_save_user_record.lua";
    private final static int BATCH_USER_COUPON_SIZE = 5000;

    @Override
    public void invoke(CouponTaskExcelObject couponTaskExcelObject, AnalysisContext analysisContext) {
        ++rowCount;
        String couponTaskId = String.valueOf(couponTask.getId());

        // 获取当前进度，判断是否已经执行过。如果已执行，则跳过即可，防止执行到一半应用宕机
        String templateTaskExecuteProgressKey = String.format(DistributionRedisConstant.TEMPLATE_TASK_EXECUTE_PROGRESS_KEY, couponTaskId);
        String progress = stringRedisTemplate.opsForValue().get(templateTaskExecuteProgressKey);
        if (StrUtil.isNotBlank(progress) && Integer.parseInt(progress) > rowCount) {
            rowCount = Integer.parseInt(progress);
            return;
        }

        // 获取 LUA 脚本，并保存到 Hutool 的单例管理容器，下次直接获取不需要加载
        DefaultRedisScript<Long> buildLuaScript = Singleton.get(STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(STOCK_DECREMENT_AND_BATCH_SAVE_USER_RECORD_LUA_PATH)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });


        // 执行 LUA 脚本进行扣减库存以及增加 Redis 用户领券记录
        String couponTemplateKey = String.format(EngineRedisConstant.COUPON_TEMPLATE_KEY, couponTemplate.getId());
        String batchUserSetKey = String.format(DistributionRedisConstant.TEMPLATE_TASK_EXECUTE_BATCH_USER_KEY, couponTaskId);
        Long combinedFiled = stringRedisTemplate.execute(buildLuaScript, ListUtil.of(couponTemplateKey, batchUserSetKey), couponTaskExcelObject.getUserId());

        // firstField 为 false 说明优惠券已经没有库存了
        boolean firstField = StockDecrementReturnCombinedUtil.extractFirstField(combinedFiled);
        if (!firstField) {
            // 同步当前执行进度到缓存
            stringRedisTemplate.opsForValue().set(templateTaskExecuteProgressKey, String.valueOf(rowCount));
            return;
        }

        // 获取用户领券集合长度
        long batchUserSetSize = StockDecrementReturnCombinedUtil.extractSecondField(combinedFiled);

        // 如果没有消息通知需求，仅在 batchUserSetSize = BATCH_USER_COUPON_SIZE 时发送消息消费。不满足条件仅记录执行进度即可
        if (batchUserSetSize < BATCH_USER_COUPON_SIZE && StrUtil.isBlank(couponTask.getNotifyType())) {
            // 同步当前 Excel 执行进度到缓存
            stringRedisTemplate.opsForValue().set(templateTaskExecuteProgressKey, String.valueOf(rowCount));
            return;
        }

        CouponTemplateExecuteEvent couponTemplateExecuteEvent = CouponTemplateExecuteEvent.builder()
                .userId(couponTaskExcelObject.getUserId())
                .mail(couponTaskExcelObject.getMail())
                .phone(couponTaskExcelObject.getPhone())
                .couponTaskId(couponTaskId)
                .notifyType(couponTask.getNotifyType())
                .shopNumber(couponTask.getShopNumber())
                .couponTemplateId(couponTemplate.getId())
                .couponTemplateConsumeRule(couponTemplate.getConsumeRule())
                .batchUserSetSize(batchUserSetSize)
                .distributionEndFlag(Boolean.FALSE)
                .build();
        couponTemplateExecuteProducer.sendMessage(couponTemplateExecuteEvent);

        // 同步当前执行进度到缓存
        stringRedisTemplate.opsForValue().set(templateTaskExecuteProgressKey, String.valueOf(rowCount));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 发送 Excel 解析完成标识，即使不满足批量保存的数量也得保存到数据库
        CouponTemplateExecuteEvent couponTemplateExecuteEvent = CouponTemplateExecuteEvent.builder()
                .distributionEndFlag(Boolean.TRUE) // 设置解析完成标识
                .shopNumber(couponTask.getShopNumber())
                .couponTemplateId(couponTemplate.getId())
                .couponTemplateConsumeRule(couponTemplate.getConsumeRule())
                .couponTaskId(String.valueOf(couponTask.getId()))
                .build();
        couponTemplateExecuteProducer.sendMessage(couponTemplateExecuteEvent);
    }
}
