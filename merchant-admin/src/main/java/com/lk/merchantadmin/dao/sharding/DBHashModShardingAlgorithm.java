package com.lk.merchantadmin.dao.sharding;

import com.lk.framework.exception.ShardingAlgorithmException;
import lombok.Getter;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * 基于 HashMod 方式自定义分库算法
 *
 * @Author : lk
 * @create 2024/8/11
 */

public final class DBHashModShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    @Getter
    private Properties props;

    private int shardingCount;
    private static final String SHARDING_COUNT_KEY = "sharding-count";

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        long id = preciseShardingValue.getValue();
        int dbSize = availableTargetNames.size();

        // 根据hash值进行取模运算，最后将结果映射到实际的目标数据库
        int mod = (int) hashShardingValue(id) % shardingCount / (shardingCount / dbSize);

        // 根据取模结果进行匹配，并返回相关数据库名称
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

    @Override
    public void init(Properties props) {
        // 保存传入的属性
        this.props = props;
        // 从属性中读取分片总数
        shardingCount = getShardingCount(props);
    }

    /**
     * 获取分片总数
     *
     * @param props
     * @return
     */
    private int getShardingCount(final Properties props) {
        // 验证配置中是否包含分片总数的键，不然就抛出异常
        checkState(props.containsKey(SHARDING_COUNT_KEY), () -> new ShardingAlgorithmException("Sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
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

    private void checkState(boolean expression, Supplier<RuntimeException> exceptionSupplier) {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }
}
