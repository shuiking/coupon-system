package com.lk.framework.exception;

import com.lk.framework.errorcode.BaseErrorCode;
import com.lk.framework.errorcode.IErrorCode;

import java.util.Optional;

/**
 * @Author : lk
 * @create 2024/8/31
 */
public class ShardingAlgorithmException extends AbstractException{
    public ShardingAlgorithmException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ShardingAlgorithmException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ShardingAlgorithmException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ShardingAlgorithmException(String message, Throwable throwable, IErrorCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.message()), throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
