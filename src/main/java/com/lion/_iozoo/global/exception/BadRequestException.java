package com.lion._iozoo.global.exception;

public class BadRequestException extends BusinessException {
    public BadRequestException() {
        super(CommonErrorCode.INVALID_INPUT);
    }

    public BadRequestException(String message) {
        super(CommonErrorCode.INVALID_INPUT, message);
    }

    protected BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
