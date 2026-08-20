package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;

import java.util.List;

public interface ListCharterRulesUseCase {
    // status가 null이면 전체(DRAFT/ADOPTED)를 반환한다.
    List<CharterRuleEntity> list(Long teamId, Long userId, CharterRuleStatus status);
}
