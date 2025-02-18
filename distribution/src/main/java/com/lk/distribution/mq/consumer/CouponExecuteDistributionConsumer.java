package com.lk.distribution.mq.consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lk.distribution.common.constant.DistributionRedisConstant;
import com.lk.distribution.common.constant.DistributionRocketMQConstant;
import com.lk.distribution.common.enums.CouponSourceEnum;
import com.lk.distribution.common.enums.CouponStatusEnum;
import com.lk.distribution.common.enums.CouponTaskStatusEnum;
import com.lk.distribution.dao.entity.CouponTaskDO;
import com.lk.distribution.dao.entity.CouponTemplateDO;
import com.lk.distribution.dao.entity.UserCouponDO;
import com.lk.distribution.dao.mapper.CouponTaskMapper;
import com.lk.distribution.dao.mapper.CouponTemplateMapper;
import com.lk.distribution.dao.mapper.UserCouponMapper;
import com.lk.distribution.dao.sharding.DBShardingUtil;
import com.lk.distribution.mq.base.MessageWrapper;
import com.lk.distribution.mq.event.CouponTemplateExecuteEvent;
import com.lk.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 优惠券执行分发到用户消费者
 *
 * @Author : lk
 * @create 2024/9/2
 */

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = DistributionRocketMQConstant.TEMPLATE_EXECUTE_DISTRIBUTION_TOPIC_KEY,
        consumerGroup = DistributionRocketMQConstant.TEMPLATE_EXECUTE_DISTRIBUTION_CG_KEY
)
@Slf4j(topic = "CouponExecuteDistributionConsumer")
public class CouponExecuteDistributionConsumer implements RocketMQListener<MessageWrapper<CouponTemplateExecuteEvent>> {

    private final UserCouponMapper userCouponMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final CouponTaskMapper couponTaskMapper;
    private final StringRedisTemplate stringRedisTemplate;

    private final static int BATCH_USER_COUPON_SIZE = 5000;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(MessageWrapper<CouponTemplateExecuteEvent> couponTemplateExecuteEventMessageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 优惠券任务执行推送@分发到用户账号 - 执行消费逻辑，消息体：{}", JSON.toJSONString(couponTemplateExecuteEventMessageWrapper));

        // 当保存用户优惠券集合达到批量保存数量
        CouponTemplateExecuteEvent event = couponTemplateExecuteEventMessageWrapper.getMessage();
        if (!event.getDistributionEndFlag() && event.getBatchUserSetSize() % BATCH_USER_COUPON_SIZE == 0) {
            decrementCouponTemplateStockAndSaveUserCouponList(event);
        }

        // 分发任务的结束标志为true，则代表已经没有excel记录了
        if (event.getDistributionEndFlag()) {
            String batchUserSetKey = String.format(DistributionRedisConstant.TEMPLATE_TASK_EXECUTE_BATCH_USER_KEY, event.getCouponTaskId());
            Long batchUserIdsSize = stringRedisTemplate.opsForValue().size(batchUserSetKey);
            event.setBatchUserSetSize(batchUserIdsSize);

            decrementCouponTemplateStockAndSaveUserCouponList(event);
            List<String> batchUserIds = stringRedisTemplate.opsForSet().pop(batchUserSetKey, Integer.MAX_VALUE);
            // 如果待保存入库用户优惠券列表如果还有值，意味着这事库存不足引起的
            if (CollUtil.isNotEmpty(batchUserIds)) {
                throw new ServiceException("库存不足");
            }

            // 确保所有用户都已经接到优惠券后，设置优惠券推送任务完成时间
            CouponTaskDO couponTaskDO = CouponTaskDO.builder()
                    .id(Long.parseLong(event.getCouponTaskId()))
                    .status(CouponTaskStatusEnum.SUCCESS.getStatus())
                    .completionTime(new Date())
                    .build();
            couponTaskMapper.updateById(couponTaskDO);
        }

    }

    private void decrementCouponTemplateStockAndSaveUserCouponList(CouponTemplateExecuteEvent event) {
        // 如果等于 0 意味着已经没有了库存，直接返回即可
        Long couponTemplateStock = decrementCouponTemplateStock(event, event.getBatchUserSetSize());
        if (couponTemplateStock <= 0L) {
            return;
        }

        // 获取 Redis 中待保存入库用户优惠券列表
        String batchUserSetKey = String.format(DistributionRedisConstant.TEMPLATE_TASK_EXECUTE_BATCH_USER_KEY, event.getCouponTaskId());
        List<String> batchUserIds = stringRedisTemplate.opsForSet().pop(batchUserSetKey, couponTemplateStock);

        // 因为 batchUserIds 数据较多，ArrayList 会进行数次扩容，为了避免额外性能消耗，直接初始化 batchUserIds 大小的数组
        List<UserCouponDO> userCouponDOList = new ArrayList<>(batchUserIds.size());
        Date now = new Date();

        // 构建 userCouponDOList 用户优惠券批量数组
        for (String each : batchUserIds) {
            DateTime validEndTime = DateUtil.offsetHour(now, JSON.parseObject(event.getCouponTemplateConsumeRule()).getInteger("validityPeriod"));
            UserCouponDO userCouponDO = UserCouponDO.builder()
                    .couponTemplateId(Long.parseLong(event.getCouponTemplateId()))
                    .userId(Long.parseLong(each))
                    .receiveTime(now)
                    .receiveCount(1) // 代表第一次领取该优惠券
                    .validStartTime(now)
                    .validEndTime(validEndTime)
                    .source(CouponSourceEnum.PLATFORM.getType())
                    .status(CouponStatusEnum.EFFECTIVE.getType())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .delFlag(0)
                    .build();
            userCouponDOList.add(userCouponDO);
        }

        // 平台优惠券每个用户限领一次。批量新增用户优惠券记录，底层通过递归方式直到全部新增成功
        batchSaveUserCouponList(Long.parseLong(event.getCouponTemplateId()), userCouponDOList);
    }


    private Long decrementCouponTemplateStock(CouponTemplateExecuteEvent event, Long decrementStockSize) {
        // 通过乐观机制自减优惠券库存记录
        String couponTemplateId = event.getCouponTemplateId();
        int decremented = couponTemplateMapper.decrementCouponTemplateStock(event.getShopNumber(), Long.parseLong(couponTemplateId), decrementStockSize);

        // 如果修改记录失败，意味着优惠券库存已不足，需要重试获取到可自减的库存数值
        if (!SqlHelper.retBool(decremented)) {
            LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                    .eq(CouponTemplateDO::getShopNumber, event.getShopNumber())
                    .eq(CouponTemplateDO::getId, Long.parseLong(couponTemplateId));
            CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOne(queryWrapper);
            return decrementCouponTemplateStock(event, couponTemplateDO.getStock().longValue());
        }
        return decrementStockSize;
    }

    private void batchSaveUserCouponList(long couponTemplateId, List<UserCouponDO> userCouponDOList) {
        // MyBatis-Plus 批量执行用户优惠券记录
        try {
            userCouponMapper.insert(userCouponDOList, userCouponDOList.size());
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof BatchExecutorException) {
                // 查询已经存在的用户优惠券记录
                List<Long> userIds = userCouponDOList.stream().map(UserCouponDO::getUserId).collect(Collectors.toList());

                List<UserCouponDO> existingUserCoupons = getExistingUserCoupons(couponTemplateId, userIds);
                // 遍历已经存在的集合，获取 userId，并从需要新增的集合中移除匹配的元素
                for (UserCouponDO each : existingUserCoupons) {
                    Long userId = each.getUserId();

                    // 使用迭代器遍历需要新增的集合，安全移除元素
                    Iterator<UserCouponDO> iterator = userCouponDOList.iterator();
                    while (iterator.hasNext()) {
                        UserCouponDO item = iterator.next();
                        if (item.getUserId().equals(userId)) {
                            iterator.remove();
                            // TODO 应该添加到 t_coupon_task_fail 并标记错误原因
                        }
                    }
                }

                // 采用递归方式重试，直到不存在重复的记录为止
                if (CollUtil.isNotEmpty(userCouponDOList)) {
                    batchSaveUserCouponList(couponTemplateId, userCouponDOList);
                }


            }
        }


    }

    /**
     * 获取已经存在的用户优惠券集合
     *
     * @param couponTemplateId 优惠券模板 ID
     * @param userIds          用户 ID 集合
     * @return 已经存在的用户优惠券模板信息集合
     */
    public List<UserCouponDO> getExistingUserCoupons(Long couponTemplateId, List<Long> userIds) {
        // 1. 将 userIds 拆分到数据库中
        Map<Integer, List<Long>> databaseUserIdMap = splitUserIdsByDatabase(userIds);

        List<UserCouponDO> result = new ArrayList<>();
        // 2. 对每个数据库执行查询
        for (Map.Entry<Integer, List<Long>> entry : databaseUserIdMap.entrySet()) {
            List<Long> userIdSubset = entry.getValue();

            // 执行查询
            List<UserCouponDO> userCoupons = queryDatabase(couponTemplateId, userIdSubset);
            result.addAll(userCoupons);
        }

        return result;
    }

    private List<UserCouponDO> queryDatabase(Long couponTemplateId, List<Long> userIds) {
        LambdaQueryWrapper<UserCouponDO> queryWrapper = Wrappers.lambdaQuery(UserCouponDO.class)
                .eq(UserCouponDO::getCouponTemplateId, couponTemplateId)
                .in(UserCouponDO::getUserId, userIds);

        return userCouponMapper.selectList(queryWrapper);
    }

    private Map<Integer, List<Long>> splitUserIdsByDatabase(List<Long> userIds) {
        Map<Integer, List<Long>> databaseUserIdMap = new HashMap<>();

        for (Long userId : userIds) {
            int databaseMod = DBShardingUtil.doUserCouponSharding(userId);
            databaseUserIdMap
                    .computeIfAbsent(databaseMod, k -> new ArrayList<>())
                    .add(userId);
        }

        return databaseUserIdMap;
    }
}

