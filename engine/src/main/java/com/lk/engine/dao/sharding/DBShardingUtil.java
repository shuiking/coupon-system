package com.lk.engine.dao.sharding;

import cn.hutool.core.lang.Singleton;

import java.util.Arrays;
import java.util.Collection;

/**
 * 针对项目中 IN 操作跨数据库场景进行拆分数据源
 *
 * @Author : lk
 * @create 2024/9/2
 */

public class DBShardingUtil {

    /**
     * 获取数据库分片算法类，在该类初始化时向 Singleton 放入实例
     */
    private static final DBHashModShardingAlgorithm dbShardingAlgorithm = Singleton.get(DBHashModShardingAlgorithm.class);

    /**
     * 解决查询用户优惠券 IN 场景跨库表不存在问题
     *
     * @param userId 分片键用户 ID
     * @return 返回 userId 所在的数据源
     */
    public static int doUserCouponSharding(Long userId) {
        return dbShardingAlgorithm.getShardingMod(userId, getAvailableDatabases().size());
    }

    /**
     * 获取可用的数据源列表
     */
    private static Collection<String> getAvailableDatabases() {
        return Arrays.asList("ds0", "ds1");
    }
}
