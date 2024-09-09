package com.lk.engine.service.handle.remind.impl;

import com.lk.engine.service.handle.remind.RemindCouponTemplate;
import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;
import org.springframework.stereotype.Component;

/**
 * 短信方式提醒用户抢券
 *
 * @Author : lk
 * @create 2024/9/9
 */

@Component
public class SendMessageRemindCouponTemplate implements RemindCouponTemplate {

    /**
     * 以短信方式提醒用户抢券
     *
     * @param remindCouponTemplateDTO 提醒所需要的信息
     */
    @Override
    public boolean remind(RemindCouponTemplateDTO remindCouponTemplateDTO) {
        return true;
    }
}
