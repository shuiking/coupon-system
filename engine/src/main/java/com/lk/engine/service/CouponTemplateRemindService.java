package com.lk.engine.service;

import com.lk.engine.dao.entity.CouponTemplateRemindDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lk.engine.dto.req.CouponTemplateRemindCancelReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindCreateReqDTO;
import com.lk.engine.dto.req.CouponTemplateRemindQueryReqDTO;
import com.lk.engine.dto.resp.CouponTemplateRemindQueryRespDTO;
import com.lk.engine.service.handle.remind.dto.RemindCouponTemplateDTO;

import java.util.List;

/**
 * @author k
 * @description 针对表【t_coupon_template_remind(用户预约提醒信息存储表)】的数据库操作Service
 * @createDate 2024-09-06 16:54:12
 */

public interface CouponTemplateRemindService extends IService<CouponTemplateRemindDO> {

    /**
     * 创建抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    boolean createCouponRemind(CouponTemplateRemindCreateReqDTO requestParam);

    /**
     * 分页查询抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    List<CouponTemplateRemindQueryRespDTO> listCouponRemind(CouponTemplateRemindQueryReqDTO requestParam);

    /**
     * 取消抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    boolean cancelCouponRemind(CouponTemplateRemindCancelReqDTO requestParam);

    /**
     * 检查是否取消抢券预约提醒
     *
     * @param requestParam 请求参数
     */
    boolean isCancelRemind(RemindCouponTemplateDTO requestParam);
}
