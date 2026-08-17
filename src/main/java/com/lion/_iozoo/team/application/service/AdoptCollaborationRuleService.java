package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.AdoptCollaborationRuleUseCase;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import com.lion._iozoo.team.domain.exception.CollaborationRuleAlreadyAdoptedException;
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
public class AdoptCollaborationRuleService implements AdoptCollaborationRuleUseCase {

    private final TeamCollaborationRuleRepository teamCollaborationRuleRepository;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public TeamCollaborationRuleEntity adopt(Long teamId, Long userId) {
        log.info("event=collaboration_rule_adopt_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            TeamCollaborationRuleEntity rule = teamCollaborationRuleRepository.findByTeamId(teamId)
                    .orElseThrow(CollaborationRuleNotFoundException::new);

            if (rule.getStatus() == CollaborationRuleStatus.ADOPTED) {
                throw new CollaborationRuleAlreadyAdoptedException();
            }

            rule.adopt();

            log.info("event=collaboration_rule_adopt_완료 teamId={}, userId={}", teamId, userId);
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=collaboration_rule_adopt_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
