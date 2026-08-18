package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;

public interface GenerateCharterDraftUseCase {
    TeamCollaborationRuleEntity generate(Long teamId, Long userId);
}
