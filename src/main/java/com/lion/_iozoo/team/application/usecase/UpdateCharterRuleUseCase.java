package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.application.result.CharterRule;

public interface UpdateCharterRuleUseCase {
    CharterRule update(Long teamId, Long userId, String ruleId, UpdateCharterRuleCommand command);
}
