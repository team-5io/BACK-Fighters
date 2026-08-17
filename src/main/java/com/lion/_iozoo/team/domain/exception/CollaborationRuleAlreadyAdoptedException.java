package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class CollaborationRuleAlreadyAdoptedException extends ConflictException {
    public CollaborationRuleAlreadyAdoptedException() {
        super(TeamErrorCode.COLLABORATION_RULE_ALREADY_ADOPTED);
    }
}
