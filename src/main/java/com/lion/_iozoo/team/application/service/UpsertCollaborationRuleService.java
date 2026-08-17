package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.application.usecase.UpsertCollaborationRuleUseCase;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertCollaborationRuleService implements UpsertCollaborationRuleUseCase {

    private final TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public TeamCollaborationRuleEntity upsert(Long teamId, Long userId, UpsertCollaborationRuleCommand command) {
        log.info("event=collaboration_rule_upsert_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            TeamCollaborationRuleEntity rule = teamCollaborationRuleRepository.findByTeamId(teamId)
                    .orElse(null);

            if (rule == null) {
                rule = teamCollaborationRuleRepository.save(
                        TeamCollaborationRuleEntity.builder()
                                .teamId(teamId)
                                .content(command.content())
                                .status(CollaborationRuleStatus.DRAFT)
                                .build()
                );
            } else {
                // 검토되지 않은 수정이 채택 상태로 남지 않도록, 수정하면 항상 DRAFT로 되돌린다.
                rule.updateContent(command.content());
            }

            log.info("event=collaboration_rule_upsert_완료 teamId={}, userId={}", teamId, userId);
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=collaboration_rule_upsert_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
