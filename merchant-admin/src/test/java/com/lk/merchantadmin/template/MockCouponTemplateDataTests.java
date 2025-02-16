package com.lk.merchantadmin.template;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.lk.merchantadmin.dao.mapper.CouponTemplateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock 优惠券模板数据，方便分库分表均衡测试
 *
 * @Author : lk
 * @create 2024/8/31
 */

@SpringBootTest
public class MockCouponTemplateDataTests {

    @Resource
    private CouponTemplateMapper couponTemplateMapper;

    private final CouponTemplateTest couponTemplateTest = new CouponTemplateTest();
    private final List<Snowflake> snowflakes = new ArrayList<>();
    private final ExecutorService executorService = new ThreadPoolExecutor(
            10,
            10,
            9999,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    private final int maxNum = 10000;

    public void beforeDataBuild() {
        for (int i = 0; i < 20; i++) {
            snowflakes.add(new Snowflake(i));
        }
    }

    @Test
    public void mockCouponTemplateTest() {
        beforeDataBuild();
        AtomicInteger count = new AtomicInteger(0);
        while (count.get() < maxNum) {
            executorService.execute(() -> {
                ThreadUtil.sleep(RandomUtil.randomInt(10));
                CouponTemplateDO couponTemplateDO = couponTemplateTest.buildCouponTemplateDO();
                couponTemplateDO.setShopNumber(snowflakes.get(RandomUtil.randomInt(20)).nextId());
                couponTemplateMapper.insert(couponTemplateDO);
                count.incrementAndGet();
            });
        }
    }
}
