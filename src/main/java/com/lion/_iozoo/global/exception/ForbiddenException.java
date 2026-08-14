package com.lion._iozoo.global.exception;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(CommonErrorCode.ACCESS_DENIED);
    }

    public ForbiddenException(String message) {
        super(CommonErrorCode.ACCESS_DENIED, message);
    }

    protected ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
