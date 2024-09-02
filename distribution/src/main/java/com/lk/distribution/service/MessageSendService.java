package com.lk.distribution.service;

import com.lk.distribution.dto.req.MessageSendReqDTO;
import com.lk.distribution.dto.resp.MessageSendRespDTO;

/**
 * 消息发送接口
 *
 * @Author : lk
 * @create 2024/9/2
 */

public interface MessageSendService {

    /**
     * 消息发送接口
     *
     * @param requestParam 消息发送请求参数
     * @return 消息发送结果
     */
    MessageSendRespDTO sendMessage(MessageSendReqDTO requestParam);
}
