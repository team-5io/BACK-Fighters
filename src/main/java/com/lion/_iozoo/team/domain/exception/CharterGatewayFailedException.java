package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class CharterGatewayFailedException extends BusinessException {
    public CharterGatewayFailedException(String context, Throwable cause) {
        super(TeamErrorCode.CHARTER_GATEWAY_FAILED, TeamErrorCode.CHARTER_GATEWAY_FAILED.getMessage(), cause);
        addContext("context", context);
    }
}
