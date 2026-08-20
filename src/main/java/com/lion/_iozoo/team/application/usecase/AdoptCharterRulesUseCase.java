package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;

import java.util.List;

public interface AdoptCharterRulesUseCase {
    List<CharterRuleEntity> adopt(Long teamId, Long userId, AdoptCharterRulesCommand command);
}
