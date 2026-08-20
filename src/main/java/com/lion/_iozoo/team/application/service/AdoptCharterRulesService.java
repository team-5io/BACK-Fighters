package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.application.usecase.AdoptCharterRulesUseCase;
import com.lion._iozoo.team.domain.exception.CharterRuleNotFoundException;
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
public class AdoptCharterRulesService implements AdoptCharterRulesUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional
    public List<CharterRuleEntity> adopt(Long teamId, Long userId, AdoptCharterRulesCommand command) {
        log.info("event=charter_rules_adopt_시작 teamId={}, userId={}, ruleCount={}",
                teamId, userId, command.ruleIds().size());

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            List<CharterRuleEntity> rules = command.ruleIds().stream()
                    .map(ruleId -> charterRuleRepository.findByIdAndTeamId(ruleId, teamId)
                            .orElseThrow(() -> new CharterRuleNotFoundException(ruleId)))
                    .toList();
            rules.forEach(CharterRuleEntity::adopt);

            log.info("event=charter_rules_adopt_완료 teamId={}, userId={}", teamId, userId);
            return rules;
        } catch (RuntimeException e) {
            log.warn("event=charter_rules_adopt_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
