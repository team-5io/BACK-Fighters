package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.CreateCharterRuleCommand;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;

public interface CreateCharterRuleUseCase {
    CharterRuleEntity create(Long teamId, Long userId, CreateCharterRuleCommand command);
}
