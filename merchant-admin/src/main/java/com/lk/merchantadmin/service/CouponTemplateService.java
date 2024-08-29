package com.lk.merchantadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.merchantadmin.dto.req.CouponTemplateNumberReqDTO;
import com.lk.merchantadmin.dto.req.CouponTemplatePageQueryReqDTO;
import com.lk.merchantadmin.dto.req.CouponTemplateSaveReqDTO;
import com.lk.merchantadmin.dto.resp.CouponTemplatePageQueryRespDTO;
import com.lk.merchantadmin.dto.resp.CouponTemplateQueryRespDTO;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Service
 * @createDate 2024-08-11 15:27:21
 */

public interface CouponTemplateService extends IService<CouponTemplateDO> {
    /**
     * 创建商家优惠券模板
     *
     * @param requestParam 请求参数
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);

    /**
     * 查询优惠券模板详情
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 优惠券模板详情
     */
    CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId);

    /**
     * 增加优惠券模板发行量
     *
     * @param requestParam 请求参数
     */
    void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam);

    /**
     * 结束优惠券模板
     *
     * @param couponTemplateId 优惠券模板 ID
     */
    void terminateCouponTemplate(String couponTemplateId);

    /**
     * 分页查询商家优惠券模板
     *
     * @param requestParam 请求参数
     * @return 商家优惠券模板分页数据
     */
    IPage<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam);
}
