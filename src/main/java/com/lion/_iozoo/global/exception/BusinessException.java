package com.lion._iozoo.global.exception;

import java.util.Map;

public abstract class BusinessException extends ApplicationException {
    protected BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    protected BusinessException(ErrorCode errorCode, String message, Map<String, Object> context) {
        super(errorCode, message, context);
    }
}
