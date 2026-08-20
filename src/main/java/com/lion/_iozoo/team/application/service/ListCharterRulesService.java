package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.ListCharterRulesUseCase;
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
public class ListCharterRulesService implements ListCharterRulesUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CharterRuleEntity> list(Long teamId, Long userId, CharterRuleStatus status) {
        log.info("event=charter_rules_list_시작 teamId={}, userId={}, status={}", teamId, userId, status);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            List<CharterRuleEntity> rules = status == null
                    ? charterRuleRepository.findByTeamId(teamId)
                    : charterRuleRepository.findByTeamIdAndStatus(teamId, status);

            log.info("event=charter_rules_list_완료 teamId={}, userId={}, count={}", teamId, userId, rules.size());
            return rules;
        } catch (RuntimeException e) {
            log.warn("event=charter_rules_list_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
