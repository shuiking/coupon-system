package com.lk.merchantadmin.dao.mapper;

import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author k
 * @description 针对表【t_coupon_template_8(优惠券模板表)】的数据库操作Mapper
 * @createDate 2024-08-11 15:27:21
 * @Entity com.lk.merchantadmin.dao.entity.CouponTemplateDO
 */
public interface CouponTemplateMapper extends BaseMapper<CouponTemplateDO> {

    /**
     * 增加优惠券模板发行量
     *
     * @param shopNumber       店铺编号
     * @param couponTemplateId 优惠券模板 ID
     * @param number           增加发行数量
     */
    int increaseNumberCouponTemplate(@Param("shopNumber") Long shopNumber, @Param("couponTemplateId") String couponTemplateId, @Param("number") Integer number);
}




