package com.lk.engine.service.handle.remind;

import cn.hutool.json.JSONUtil;
import com.lk.engine.common.enums.CouponRemindTypeEnum;
import com.lk.engine.mq.event.CouponRemindEvent;
import com.lk.engine.mq.producer.CouponRemindProducer;
import com.lk.engine.service.CouponTemplateRemindService;
import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;
import com.lk.engine.service.handle.remind.impl.SendEmailRemindCouponTemplate;
import com.lk.engine.service.handle.remind.impl.SendMessageRemindCouponTemplate;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.*;

import static com.lk.engine.common.constant.EngineRedisConstant.COUPON_REMIND_CHECK_KEY;

/**
 * 执行相应的抢券提醒
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Component
@RequiredArgsConstructor
public class ExecuteRemindCouponTemplate {

    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final CouponTemplateRemindService couponTemplateRemindService;
    private final SendEmailRemindCouponTemplate sendEmailRemindCouponTemplate;
    private final SendMessageRemindCouponTemplate sendMessageRemindCouponTemplate;

    public static final String REDIS_BLOCKING_DEQUE = "COUPON_REMIND_QUEUE";

    // 提醒用户属于 IO 密集型任务
    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() << 1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 执行提醒
     *
     * @param remindDTO 需要的信息
     */
    public void executeRemindCouponTemplate(RemindCouponTemplateDTO remindDTO) {
        executorService.execute(() -> {
            // 用户没取消预约，则发出提醒
            // 假设刚把消息提交到线程池，突然应用宕机了，我们通过延迟队列进行兜底 Refresh
            if (!couponTemplateRemindService.isCancelRemind(remindDTO)) {
                RBlockingDeque<String> blockingDeque = redissonClient.getBlockingDeque(REDIS_BLOCKING_DEQUE);
                RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                String key = String.format(COUPON_REMIND_CHECK_KEY, remindDTO.getUserId(), remindDTO.getCouponTemplateId(), remindDTO.getRemindTime(), remindDTO.getType());
                stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(remindDTO));
                delayedQueue.offer(key, 5, TimeUnit.SECONDS);

                // 向用户发起消息提醒
                switch (Objects.requireNonNull(CouponRemindTypeEnum.getByType(remindDTO.getType()))) {
                    case EMAIL:
                        sendEmailRemindCouponTemplate.remind(remindDTO);
                        break;
                    case MESSAGE:
                        sendMessageRemindCouponTemplate.remind(remindDTO);
                        break;
                }

                // 提醒用户后删除 Key
                stringRedisTemplate.delete(key);
            }
        });
    }

    @Component
    @RequiredArgsConstructor
    class RefreshCouponRemindDelayQueueRunner implements CommandLineRunner {

        private final CouponRemindProducer couponRemindProducer;
        private final RedissonClient redissonClient;
        private final StringRedisTemplate stringRedisTemplate;
        private final Logger LOG = LoggerFactory.getLogger(RefreshCouponRemindDelayQueueRunner.class);

        @Override
        public void run(String... args) {
            Executors.newSingleThreadExecutor(
                            runnable -> {
                                Thread thread = new Thread(runnable);
                                thread.setName("delay_coupon-remind_consumer");
                                thread.setDaemon(Boolean.TRUE);
                                return thread;
                            })
                    .execute(() -> {
                        RBlockingDeque<String> blockingDeque = redissonClient.getBlockingDeque(REDIS_BLOCKING_DEQUE);
                        for (; ; ) {
                            try {
                                // 获取延迟队列待消费 Key
                                String key = blockingDeque.take();
                                LOG.info("检查key：{} 是否被消费", key);
                                if (null != stringRedisTemplate.hasKey(key)) {
                                    // Redis 中还存在该 Key，说明任务没被消费完，则可能是消费机器宕机了，重新投递消息
                                    CouponRemindEvent couponRemindEvent = JSONUtil.toBean(stringRedisTemplate.opsForValue().get(key), CouponRemindEvent.class);
                                    couponRemindProducer.sendMessage(couponRemindEvent);
                                    stringRedisTemplate.delete(key);
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    });
        }
    }
}
