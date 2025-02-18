package com.lk.settlement.common.constant;

/**
 * 分布式 Redis 缓存引擎层常量类
 *
 * @Author : lk
 * @create 2025/2/18
 */
public final class EngineRedisConstant {

    /**
     * 优惠券模板缓存 Key
     */
    public static final String COUPON_TEMPLATE_KEY = "coupon_engine:template:%s";

    /**
     * 用户已领取优惠券列表模板 Key
     */
    public static final String USER_COUPON_TEMPLATE_LIST_KEY = "coupon_engine:user-template-list:%s";
}
