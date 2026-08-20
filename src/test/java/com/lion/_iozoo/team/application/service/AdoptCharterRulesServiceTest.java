package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.AdoptCharterRulesCommand;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.domain.exception.CharterRuleNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptCharterRulesServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private CharterRuleRepository charterRuleRepository;

    private AdoptCharterRulesService sut() {
        return new AdoptCharterRulesService(teamPermissionChecker, charterRuleRepository);
    }

    private CharterRuleEntity rule(Long id) {
        return CharterRuleEntity.builder()
                .id(id).teamId(1L).title("규칙" + id).content("내용")
                .status(CharterRuleStatus.DRAFT).build();
    }

    @Test
    void 관리자면_지정한_규칙들을_채택한다() {
        when(charterRuleRepository.findByIdAndTeamId(1L, 1L)).thenReturn(Optional.of(rule(1L)));
        when(charterRuleRepository.findByIdAndTeamId(2L, 1L)).thenReturn(Optional.of(rule(2L)));

        List<CharterRuleEntity> result = sut().adopt(1L, 10L, new AdoptCharterRulesCommand(List.of(1L, 2L)));

        assertThat(result).allMatch(rule -> rule.getStatus() == CharterRuleStatus.ADOPTED);
    }

    @Test
    void 다른_팀_소속이거나_없는_규칙이_섞이면_예외() {
        when(charterRuleRepository.findByIdAndTeamId(1L, 1L)).thenReturn(Optional.of(rule(1L)));
        when(charterRuleRepository.findByIdAndTeamId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().adopt(1L, 10L, new AdoptCharterRulesCommand(List.of(1L, 999L))))
                .isInstanceOf(CharterRuleNotFoundException.class);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().adopt(1L, 99L, new AdoptCharterRulesCommand(List.of(1L))))
                .isInstanceOf(ForbiddenException.class);
    }
}
