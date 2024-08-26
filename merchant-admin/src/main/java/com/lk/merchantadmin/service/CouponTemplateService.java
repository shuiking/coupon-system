package com.lk.merchantadmin.service;

import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.merchantadmin.dto.resp.CouponTemplateQueryRespDTO;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Service
 * @createDate 2024-08-11 15:27:21
 */

public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 查询优惠券模板详情
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 优惠券模板详情
     */
    CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId);
}
