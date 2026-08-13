package com.lion._iozoo.global.exception;

public class ConflictException extends BusinessException {
    public ConflictException() {
        super(CommonErrorCode.CONFLICT);
    }

    public ConflictException(String message) {
        super(CommonErrorCode.CONFLICT, message);
    }

    protected ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
