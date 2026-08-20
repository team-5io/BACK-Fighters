package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.result.CharterRule;

import java.util.List;

public interface GenerateCharterDraftUseCase {
    List<CharterRule> generate(Long teamId, Long userId);
}
