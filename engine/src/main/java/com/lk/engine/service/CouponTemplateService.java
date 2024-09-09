package com.lk.engine.service;

import com.lk.engine.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.engine.dto.req.CouponTemplateQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateQueryRespDTO;

import java.util.List;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Service
 * @createDate 2024-09-06 16:54:12
 */

public interface CouponTemplateService extends IService<CouponTemplateDO> {

    /**
     * 查询优惠券模板
     *
     * @param requestParam 请求参数
     * @return 优惠券模板信息
     */
    CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam);

    /**
     * 根据优惠券id集合查询出券信息
     *
     * @param couponTemplateIds 优惠券id集合
     */
    List<CouponTemplateDO> listCouponTemplateById(List<Long> couponTemplateIds, List<Long> shopNumbers);
}
