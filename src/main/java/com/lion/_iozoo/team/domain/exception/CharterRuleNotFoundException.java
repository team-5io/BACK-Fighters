package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class CharterRuleNotFoundException extends NotFoundException {
    public CharterRuleNotFoundException(Long ruleId) {
        super(TeamErrorCode.CHARTER_RULE_NOT_FOUND, TeamErrorCode.CHARTER_RULE_NOT_FOUND.getMessage());
        addContext("ruleId", ruleId);
    }
}
