package com.lion._iozoo.user.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
