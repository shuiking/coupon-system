package com.lk.distribution.dao.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * 基于 HashMod 方式自定义分表算法
 *
 * @Author : lk
 * @create 2024/8/11
 */
public final class TableHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        long id = preciseShardingValue.getValue();
        int shardingCount = availableTargetNames.size();

        // 根据hash值进行取模运算，获取映射到实际表的结果
        int mod = (int) hashShardingValue(id) % shardingCount;

        // 根据取模结果进行匹配，并返回相关表名称
        int index = 0;
        for (String targetName : availableTargetNames) {
            if (index == mod) {
                return targetName;
            }
            index++;
        }
        throw new IllegalArgumentException("No target found for value: " + id);
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        // 暂无范围分片场景，默认返回空
        return Collections.emptyList();
    }

    /**
     * 计算分片值的哈希值
     *
     * @param shardingValue
     * @return
     */
    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties props) {

    }
}
