package com.lk.framework.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 全局通用返回对象
 *
 * @Author : lk
 * @create 2024/8/11
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 正确返回码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public boolean isFail() {
        return !isSuccess();
    }


}
