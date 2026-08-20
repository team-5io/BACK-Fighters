package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;

public interface AdoptCharterRulesUseCase {
    void adopt(Long teamId, Long userId, AdoptCharterRulesCommand command);
}
