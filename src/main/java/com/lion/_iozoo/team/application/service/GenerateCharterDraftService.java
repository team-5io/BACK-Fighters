package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRuleDraft;
import com.lion._iozoo.team.application.usecase.GenerateCharterDraftUseCase;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateCharterDraftService implements GenerateCharterDraftUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final RequestCharterDraftPort requestCharterDraftPort;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional
    public List<CharterRuleEntity> generate(Long teamId, Long userId) {
        log.info("event=charter_draft_generate_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            List<CharterRuleDraft> drafts = requestCharterDraftPort.requestDraft(teamId);
            List<CharterRuleEntity> saved = drafts.stream()
                    .map(draft -> charterRuleRepository.save(CharterRuleEntity.builder()
                            .teamId(teamId)
                            .title(draft.title())
                            .content(draft.description())
                            .status(CharterRuleStatus.DRAFT)
                            .build()))
                    .toList();

            log.info("event=charter_draft_generate_완료 teamId={}, userId={}, ruleCount={}", teamId, userId, saved.size());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=charter_draft_generate_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
