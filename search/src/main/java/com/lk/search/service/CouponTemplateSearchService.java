package com.lk.search.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lk.search.dto.req.CouponTemplatePageQueryReqDTO;
import com.lk.search.dto.resp.CouponTemplatePageQueryRespDTO;

/**
 * 优惠券模板搜索业务逻辑层
 *
 * @Author : lk
 * @create 2024/9/11
 */

public interface CouponTemplateSearchService {

    /**
     * 分页查询商家优惠券模板
     *
     * @param requestParam 请求参数
     * @return 商家优惠券模板分页数据
     */
    IPage<CouponTemplatePageQueryRespDTO> pageQueryCouponTemplate(CouponTemplatePageQueryReqDTO requestParam);

}
