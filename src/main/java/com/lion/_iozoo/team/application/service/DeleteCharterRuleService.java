package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.DeleteCharterRuleUseCase;
import com.lion._iozoo.team.domain.exception.CharterRuleNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCharterRuleService implements DeleteCharterRuleUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional
    public void delete(Long teamId, Long userId, Long ruleId) {
        log.info("event=charter_rule_delete_시작 teamId={}, userId={}, ruleId={}", teamId, userId, ruleId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            CharterRuleEntity rule = charterRuleRepository.findByIdAndTeamId(ruleId, teamId)
                    .orElseThrow(() -> new CharterRuleNotFoundException(ruleId));
            charterRuleRepository.delete(rule);

            log.info("event=charter_rule_delete_완료 teamId={}, userId={}, ruleId={}", teamId, userId, ruleId);
        } catch (RuntimeException e) {
            log.warn("event=charter_rule_delete_실패 teamId={}, userId={}, ruleId={}, reason={}",
                    teamId, userId, ruleId, e.getMessage(), e);
            throw e;
        }
    }
}
