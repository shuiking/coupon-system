package com.lk.engine.service.handle.remind;

import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;

/**
 * 优惠券抢券提醒接口
 *
 * @Author : lk
 * @create 2024/9/9
 */

public interface RemindCouponTemplate {

    /**
     * 提醒用户抢券
     *
     * @param remindCouponTemplateDTO 提醒所需要的信息
     */
    boolean remind(RemindCouponTemplateDTO remindCouponTemplateDTO);
}
