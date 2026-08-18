package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRuleDraft;
import com.lion._iozoo.team.application.usecase.GenerateCharterDraftUseCase;
import com.lion._iozoo.team.application.usecase.UpsertCollaborationRuleUseCase;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateCharterDraftService implements GenerateCharterDraftUseCase {

    private final RequestCharterDraftPort requestCharterDraftPort;
    private final UpsertCollaborationRuleUseCase upsertCollaborationRuleUseCase;

    @Override
    @Transactional
    public TeamCollaborationRuleEntity generate(Long teamId, Long userId) {
        log.info("event=charter_draft_generate_시작 teamId={}, userId={}", teamId, userId);

        try {
            List<CharterRuleDraft> drafts = requestCharterDraftPort.requestDraft(teamId);
            String content = formatDrafts(drafts);

            // ADMIN 권한 체크는 upsert 내부에서 수행 — 여기서 중복 체크 안 함.
            TeamCollaborationRuleEntity rule = upsertCollaborationRuleUseCase.upsert(
                    teamId, userId, new UpsertCollaborationRuleCommand(content, CollaborationRuleStatus.DRAFT));

            log.info("event=charter_draft_generate_완료 teamId={}, userId={}, ruleCount={}", teamId, userId, drafts.size());
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=charter_draft_generate_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }

    // AI가 여러 규칙(N개)을 반환하는데, BE는 팀당 협업 규칙을 단일 텍스트 한 덩어리로만 저장하므로
    // 번호 매긴 목록 형태로 합친다. 개별 규칙 단위 수정/채택은 지원하지 않음(팀이 합쳐진 텍스트를
    // 통으로 PUT /{teamId}/charter로 다듬는 구조).
    private String formatDrafts(List<CharterRuleDraft> drafts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < drafts.size(); i++) {
            CharterRuleDraft draft = drafts.get(i);
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(i + 1).append(". ").append(draft.title()).append("\n").append(draft.description());
        }
        return sb.toString();
    }
}
