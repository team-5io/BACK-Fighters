package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpsertCollaborationRuleCommand;
import com.lion._iozoo.team.application.usecase.UpsertCollaborationRuleUseCase;
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
                                .status(command.status())
                                .build()
                );
            } else {
                rule.update(command.content(), command.status());
            }

            log.info("event=collaboration_rule_upsert_완료 teamId={}, userId={}, status={}",
                    teamId, userId, command.status());
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=collaboration_rule_upsert_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
