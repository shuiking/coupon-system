package com.lk.framework.exception;

import com.lk.framework.errorcode.BaseErrorCode;
import com.lk.framework.errorcode.IErrorCode;

/**
 * 远程服务调用异常
 *
 * @Author : lk
 * @create 2024/8/11
 */
public class RemoteException extends AbstractException {
    public RemoteException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
