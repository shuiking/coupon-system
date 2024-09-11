package com.lk.search.mq.consumer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.lk.search.common.constant.SearchRockerMQConstant;
import com.lk.search.common.enums.CouponTemplateStatusEnum;
import com.lk.search.dao.entity.CouponTemplateDO;
import com.lk.search.dao.entity.CouponTemplateDoc;
import com.lk.search.mq.event.CanalBinlogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通过 Canal 监听优惠券模板表 Binlog 投递消息队列消费
 *
 * @Author : lk
 * @create 2024/9/11
 */

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = SearchRockerMQConstant.TEMPLATE_COUPON_BINLOG_SYNC_TOPIC_KEY,
        consumerGroup = SearchRockerMQConstant.TEMPLATE_COUPON_BINLOG_SYNC_CG_KEY
)
public class CanalBinlogSyncCouponTemplateConsumer implements RocketMQListener<CanalBinlogEvent> {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public void onMessage(CanalBinlogEvent canalBinlogEvent) {
        log.info("{}", canalBinlogEvent);

        Map<String, Object> first = CollUtil.getFirst(canalBinlogEvent.getData());
        CouponTemplateDO couponTemplate = BeanUtil.toBean(first, CouponTemplateDO.class);
        CouponTemplateDoc couponTemplateDoc = BeanUtil.copyProperties(couponTemplate, CouponTemplateDoc.class);

        switch (canalBinlogEvent.getType()) {
            // 新增优惠券
            case "INSERT": {
                elasticsearchTemplate.save(couponTemplateDoc);
                break;
            }
            case "UPDATE": {
                // 优惠券已结束或已删除, 都将该数据从ES中移除, 否则进行更新操作
                if (CouponTemplateStatusEnum.EXPIRED.getCode().equals(couponTemplate.getStatus())
                        || CouponTemplateStatusEnum.DELETED.getCode().equals(couponTemplate.getDelFlag())) {
                    elasticsearchTemplate.delete(couponTemplateDoc);
                    break;
                }

                // 更新或者插入数据
                elasticsearchTemplate.save(couponTemplateDoc);
                break;
            }
            // 硬删除优惠券, 一般业务上不会走该分支, 为了避免直接操作数据库导致数据不一致, 因此也加上该段逻辑
            case "DELETE": {
                elasticsearchTemplate.delete(couponTemplateDoc);
                break;
            }
        }
    }
}
