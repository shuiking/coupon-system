package com.lk.distribution.service.impl;

import com.lk.distribution.common.enums.SendMessageMarkCovertEnum;
import com.lk.distribution.dto.req.MessageSendReqDTO;
import com.lk.distribution.dto.resp.MessageSendRespDTO;
import com.lk.distribution.service.MessageSendService;
import com.lk.distribution.service.basics.DistributionExecuteStrategy;
import org.springframework.stereotype.Service;

/**
 * 应用消息发送接口实现类
 *
 * @Author : lk
 * @create 2024/9/2
 */

@Service
public class ApplicationMessageSendServiceImpl implements MessageSendService, DistributionExecuteStrategy<MessageSendReqDTO, MessageSendRespDTO> {

    @Override
    public MessageSendRespDTO sendMessage(MessageSendReqDTO requestParam) {
        return null;
    }

    @Override
    public String mark() {
        return SendMessageMarkCovertEnum.APPLICATION.name();
    }

    @Override
    public MessageSendRespDTO executeResp(MessageSendReqDTO requestParam) {
        return sendMessage(requestParam);
    }
}
