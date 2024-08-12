package com.lk.framework.errorcode;

/**
 * 定义错误码抽象接口
 *
 * @Author : lk
 * @create 2024/8/11
 */
public interface IErrorCode {
    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}
