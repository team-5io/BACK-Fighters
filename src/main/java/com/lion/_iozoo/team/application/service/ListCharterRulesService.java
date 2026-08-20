package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.RequestCharterRulesPort;
import com.lion._iozoo.team.application.result.CharterRule;
import com.lion._iozoo.team.application.usecase.ListCharterRulesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListCharterRulesService implements ListCharterRulesUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final RequestCharterRulesPort requestCharterRulesPort;

    @Override
    public List<CharterRule> list(Long teamId, Long userId, String status) {
        log.info("event=charter_rules_list_시작 teamId={}, userId={}, status={}", teamId, userId, status);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            List<CharterRule> rules = requestCharterRulesPort.listRules(teamId, status);

            log.info("event=charter_rules_list_완료 teamId={}, userId={}, count={}", teamId, userId, rules.size());
            return rules;
        } catch (RuntimeException e) {
            log.warn("event=charter_rules_list_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
