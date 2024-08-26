package com.lk.merchantadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.merchantadmin.dao.entity.CouponTaskDO;
import com.lk.merchantadmin.dto.req.CouponTaskCreateReqDTO;

/**
 * @author k
 * @description 针对表【t_coupon_task(优惠券模板发送任务表)】的数据库操作Service
 * @createDate 2024-08-11 15:21:26
 */

public interface CouponTaskService extends IService<CouponTaskDO> {

    /**
     * 商家创建优惠券推送任务
     *
     * @param requestParam 请求参数
     */
    void createCouponTask(CouponTaskCreateReqDTO requestParam);
}
