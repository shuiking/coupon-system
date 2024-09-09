package com.lk.engine.dao.mapper;

import com.lk.engine.dao.entity.CouponTemplateDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author k
 * @description 针对表【t_coupon_template(优惠券模板表)】的数据库操作Mapper
 * @createDate 2024-09-06 16:54:12
 * @Entity com.lk.engine.dao.entity.CouponTemplateDO
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




