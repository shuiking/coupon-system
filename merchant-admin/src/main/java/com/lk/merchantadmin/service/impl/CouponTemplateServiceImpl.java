package com.lk.merchantadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lk.merchantadmin.common.context.UserContext;
import com.lk.merchantadmin.dao.entity.CouponTemplateDO;
import com.lk.merchantadmin.dto.resp.CouponTemplateQueryRespDTO;
import com.lk.merchantadmin.service.CouponTemplateService;
import com.lk.merchantadmin.dao.mapper.CouponTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author k
 * @description 针对表【t_coupon_template_8(优惠券模板表)】的数据库操作Service实现
 * @createDate 2024-08-11 15:27:21
 */

@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplateDO>
        implements CouponTemplateService {

    private final CouponTemplateMapper couponTemplateMapper;


    @Override
    public CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId) {
        LambdaQueryWrapper<CouponTemplateDO> queryWrapper = Wrappers.lambdaQuery(CouponTemplateDO.class)
                .eq(CouponTemplateDO::getShopNumber, UserContext.getShopNumber())
                .eq(CouponTemplateDO::getId, couponTemplateId);

        CouponTemplateDO couponTemplateDO = couponTemplateMapper.selectOne(queryWrapper);
        return BeanUtil.toBean(couponTemplateDO, CouponTemplateQueryRespDTO.class);
    }
}




