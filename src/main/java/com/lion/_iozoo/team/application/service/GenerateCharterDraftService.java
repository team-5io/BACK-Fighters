package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRule;
import com.lion._iozoo.team.application.usecase.GenerateCharterDraftUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateCharterDraftService implements GenerateCharterDraftUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final RequestCharterDraftPort requestCharterDraftPort;

    @Override
    public List<CharterRule> generate(Long teamId, Long userId) {
        log.info("event=charter_draft_generate_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            List<CharterRule> rules = requestCharterDraftPort.requestDraft(teamId);

            log.info("event=charter_draft_generate_완료 teamId={}, userId={}, ruleCount={}", teamId, userId, rules.size());
            return rules;
        } catch (RuntimeException e) {
            log.warn("event=charter_draft_generate_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
