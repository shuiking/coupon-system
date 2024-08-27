package com.lk.merchantadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.merchantadmin.dao.entity.CouponTaskDO;
import com.lk.merchantadmin.dto.req.CouponTaskCreateReqDTO;
import com.lk.merchantadmin.dto.req.CouponTaskPageQueryReqDTO;
import com.lk.merchantadmin.dto.resp.CouponTaskPageQueryRespDTO;
import com.lk.merchantadmin.dto.resp.CouponTaskQueryRespDTO;

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

    /**
     * 查询优惠券推送任务详情
     *
     * @param taskId 推送任务 ID
     * @return 优惠券推送任务详情
     */
    CouponTaskQueryRespDTO findCouponTaskById(String taskId);

    /**
     * 分页查询商家优惠券推送任务
     *
     * @param requestParam 请求参数
     * @return 商家优惠券推送任务分页数据
     */
    IPage<CouponTaskPageQueryRespDTO> pageQueryCouponTask(CouponTaskPageQueryReqDTO requestParam);
}
