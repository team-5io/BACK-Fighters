package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.CreateCharterRuleCommand;
import com.lion._iozoo.team.application.usecase.CreateCharterRuleUseCase;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCharterRuleService implements CreateCharterRuleUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final CharterRuleRepository charterRuleRepository;

    @Override
    @Transactional
    public CharterRuleEntity create(Long teamId, Long userId, CreateCharterRuleCommand command) {
        log.info("event=charter_rule_create_시작 teamId={}, userId={}", teamId, userId);

        try {
            teamPermissionChecker.requireAdmin(teamId, userId);

            CharterRuleEntity rule = charterRuleRepository.save(CharterRuleEntity.builder()
                    .teamId(teamId)
                    .title(command.title())
                    .content(command.content())
                    .status(CharterRuleStatus.DRAFT)
                    .build());

            log.info("event=charter_rule_create_완료 teamId={}, userId={}, ruleId={}", teamId, userId, rule.getId());
            return rule;
        } catch (RuntimeException e) {
            log.warn("event=charter_rule_create_실패 teamId={}, userId={}, reason={}", teamId, userId, e.getMessage(), e);
            throw e;
        }
    }
}
