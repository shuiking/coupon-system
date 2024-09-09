package com.lk.engine.common.constant;

/**
 * 优惠券引擎层服务 RocketMQ 常量类
 *
 * @Author : lk
 * @create 2024/9/9
 */

public final class EngineRockerMQConstant {

    /**
     * 用户优惠券到期后关闭 Topic Key
     */
    public static final String USER_COUPON_DELAY_CLOSE_TOPIC_KEY = "coupon_engine-service_user-coupon-delay-close_topic${unique-name:}";

    /**
     * 用户优惠券到期后关闭消费者组 Key
     */
    public static final String USER_COUPON_DELAY_CLOSE_CG_KEY = "coupon_engine-service_user-coupon-delay-close_cg${unique-name:}";

    /**
     * 提醒用户抢券 Topic Key
     */
    public static final String COUPON_TEMPLATE_REMIND_TOPIC_KEY = "coupon_engine-service_coupon-remind_topic${unique-name:}";

    /**
     * 提醒用户抢券消费者组 Key
     */
    public static final String COUPON_TEMPLATE_REMIND_CG_KEY = "coupon_engine-service_coupon-remind_cg${unique-name:}";

    /**
     * Canal 监听用户优惠券表 Binlog Topic Key
     */
    public static final String USER_COUPON_BINLOG_SYNC_TOPIC_KEY = "coupon_canal_engine-service_common-sync_topic${unique-name:}";

    /**
     * Canal 监听用户优惠券表 Binlog 消费者组 Key
     */
    public static final String USER_COUPON_BINLOG_SYNC_CG_KEY = "coupon_canal_engine-service_common-sync_cg${unique-name:}";
}
