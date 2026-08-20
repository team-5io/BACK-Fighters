package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.application.port.out.RequestAdoptCharterRulesPort;
import com.lion._iozoo.team.application.usecase.AdoptCharterRulesUseCase;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptCharterRulesService implements AdoptCharterRulesUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadUserPort loadUserPort;
    private final RequestAdoptCharterRulesPort requestAdoptCharterRulesPort;

    @Override
    public void adopt(Long teamId, Long userId, AdoptCharterRulesCommand command) {
        log.info("event=charter_rules_adopt_시작 teamId={}, userId={}, ruleCount={}",
                teamId, userId, command.ruleIds().size());

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            User admin = loadUserPort.loadUserById(userId).orElseThrow(UserNotFoundException::new);
            requestAdoptCharterRulesPort.adopt(teamId, command.ruleIds(), admin.getPublicId());

            log.info("event=charter_rules_adopt_완료 teamId={}, userId={}", teamId, userId);
        } catch (RuntimeException e) {
            log.warn("event=charter_rules_adopt_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
