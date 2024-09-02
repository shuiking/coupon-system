package com.lk.distribution.dao.mapper;

import com.lk.distribution.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Mapper
 * @createDate 2024-08-30 01:46:00
 * @Entity com.lk.distribution.dao.entity.TCouponTemplate8
 */

public interface CouponTemplateMapper extends BaseMapper<CouponTemplateDO> {
    /**
     * 自减优惠券模板库存
     *
     * @param couponTemplateId 优惠券模板 ID
     * @return 是否发生记录变更
     */
    int decrementCouponTemplateStock(@Param("shopNumber") Long shopNumber, @Param("couponTemplateId") Long couponTemplateId, @Param("decrementStock") Long decrementStock);
}




