package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;

public interface AdoptCollaborationRuleUseCase {
    TeamCollaborationRuleEntity adopt(Long teamId, Long userId);
}
