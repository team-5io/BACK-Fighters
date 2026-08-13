package com.lion._iozoo.user.domain.exception;

import com.lion._iozoo.global.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {
    public InvalidCredentialsException() {
        super(UserErrorCode.INVALID_CREDENTIALS, UserErrorCode.INVALID_CREDENTIALS.getMessage());
    }
}
