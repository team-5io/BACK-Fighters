package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class CollaborationRuleNotFoundException extends NotFoundException {
    public CollaborationRuleNotFoundException() {
        super(TeamErrorCode.COLLABORATION_RULE_NOT_FOUND);
    }
}
