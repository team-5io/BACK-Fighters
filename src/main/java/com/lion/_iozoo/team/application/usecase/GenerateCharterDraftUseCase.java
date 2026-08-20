package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;

import java.util.List;

public interface GenerateCharterDraftUseCase {
    List<CharterRuleEntity> generate(Long teamId, Long userId);
}
