package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class InvitedUserNotFoundException extends NotFoundException {
    public InvitedUserNotFoundException() {
        super(TeamErrorCode.INVITED_USER_NOT_FOUND);
    }
}
