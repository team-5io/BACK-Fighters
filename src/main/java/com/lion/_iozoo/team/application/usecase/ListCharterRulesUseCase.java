package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.result.CharterRule;

import java.util.List;

public interface ListCharterRulesUseCase {
    // status가 null이면 draft/adopted/archived 전체를 반환한다.
    List<CharterRule> list(Long teamId, Long userId, String status);
}
