package com.lion._iozoo.global.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(CommonErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(CommonErrorCode.UNAUTHORIZED, message);
    }

    protected UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
