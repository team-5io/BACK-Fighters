package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.GetCollaborationRuleUseCase;
import com.lion._iozoo.team.domain.exception.CollaborationRuleNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamCollaborationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCollaborationRuleService implements GetCollaborationRuleUseCase {

    private final TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public TeamCollaborationRuleEntity getRule(Long teamId, Long userId) {
        log.info("event=collaboration_rule_get_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            TeamCollaborationRuleEntity rule = teamCollaborationRuleRepository.findByTeamId(teamId)
                    .orElseThrow(CollaborationRuleNotFoundException::new);

            log.info("event=collaboration_rule_get_완료 teamId={}, userId={}", teamId, userId);
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=collaboration_rule_get_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
