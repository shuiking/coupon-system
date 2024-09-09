package com.lk.engine.service;

import com.lk.engine.dao.entity.UserCouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.engine.dto.req.CouponTemplateRedeemReqDTO;

/**
 * @author k
 * @description 针对表【t_user_coupon(用户优惠券表)】的数据库操作Service
 * @createDate 2024-09-06 16:54:12
 */

public interface UserCouponService extends IService<UserCouponDO> {

    void redeemUserCoupon(CouponTemplateRedeemReqDTO requestParam);

}
