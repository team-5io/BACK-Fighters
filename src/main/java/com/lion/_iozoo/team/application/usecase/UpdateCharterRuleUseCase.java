package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;

public interface UpdateCharterRuleUseCase {
    CharterRuleEntity update(Long teamId, Long userId, Long ruleId, UpdateCharterRuleCommand command);
}
