package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.application.usecase.UpdateCharterRuleUseCase;
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
public class UpdateCharterRuleService implements UpdateCharterRuleUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional
    public CharterRuleEntity update(Long teamId, Long userId, Long ruleId, UpdateCharterRuleCommand command) {
        log.info("event=charter_rule_update_시작 teamId={}, userId={}, ruleId={}", teamId, userId, ruleId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            CharterRuleEntity rule = charterRuleRepository.findByIdAndTeamId(ruleId, teamId)
                    .orElseThrow(() -> new CharterRuleNotFoundException(ruleId));
            rule.update(command.title(), command.content());

            log.info("event=charter_rule_update_완료 teamId={}, userId={}, ruleId={}", teamId, userId, ruleId);
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=charter_rule_update_실패 teamId={}, userId={}, ruleId={}, reason={}",
                    teamId, userId, ruleId, e.getMessage(), e);
            throw e;
        }
    }
}
