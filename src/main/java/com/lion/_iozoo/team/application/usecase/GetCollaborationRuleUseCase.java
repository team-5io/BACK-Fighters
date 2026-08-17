package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;

public interface GetCollaborationRuleUseCase {
    TeamCollaborationRuleEntity getRule(Long teamId, Long userId);
}
