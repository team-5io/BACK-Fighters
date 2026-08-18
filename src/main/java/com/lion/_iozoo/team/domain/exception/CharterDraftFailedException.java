package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class CharterDraftFailedException extends BusinessException {
    public CharterDraftFailedException(Long teamId, Throwable cause) {
        super(TeamErrorCode.CHARTER_DRAFT_FAILED, TeamErrorCode.CHARTER_DRAFT_FAILED.getMessage(), cause);
        addContext("teamId", teamId);
    }
}
