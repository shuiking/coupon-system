package com.lk.merchantadmin.service.base.chain;

import org.springframework.core.Ordered;

/**
 * 抽象商家后管业务责任链组件
 *
 * @Author : lk
 * @create 2024/8/27
 */
public interface MerchantAdminAbstractChainHandler<T> extends Ordered {

    /**
     * 执行责任链逻辑
     *
     * @param requestParam 责任链执行入参
     */
    void handler(T requestParam);

    /**
     * @return 责任链组件标识
     */
    String mark();
}
