package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;

public interface UpsertCollaborationRuleUseCase {
    TeamCollaborationRuleEntity upsert(Long teamId, Long userId, UpsertCollaborationRuleCommand command);
}
