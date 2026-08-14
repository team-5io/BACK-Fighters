package com.lion._iozoo.user.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class EmailDuplicateException extends ConflictException {
    public EmailDuplicateException() {
        super(UserErrorCode.EMAIL_DUPLICATE);
    }
}
