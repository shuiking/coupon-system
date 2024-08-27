package com.lk.merchantadmin.service.handler.filter;

import cn.hutool.core.util.ObjectUtil;
import com.lk.merchantadmin.dto.req.CouponTemplateSaveReqDTO;
import com.lk.merchantadmin.enums.DiscountTargetEnum;
import com.lk.merchantadmin.service.base.chain.MerchantAdminAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.lk.merchantadmin.enums.ChainBizMarkEnum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * 验证优惠券创建接口参数是否正确责任链｜验证参数数据是否正确
 *
 * @Author : lk
 * @create 2024/8/28
 */

@Component
public class CouponTemplateCreateParamVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO> {

    @Override
    public void handler(CouponTemplateSaveReqDTO requestParam) {
        if (ObjectUtil.equal(requestParam.getTarget(), DiscountTargetEnum.PRODUCT_SPECIFIC)) {
            // 调用商品中台验证商品是否存在，如果不存在抛出异常
            // ......
        }
    }

    @Override
    public String mark() {
        return MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
