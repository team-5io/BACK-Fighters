package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.UpdateCharterRuleCommand;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import com.lion._iozoo.team.domain.exception.CharterRuleNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleEntity;
import com.lion._iozoo.team.infrastructure.persistence.CharterRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCharterRuleServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private CharterRuleRepository charterRuleRepository;

    private UpdateCharterRuleService sut() {
        return new UpdateCharterRuleService(teamPermissionChecker, charterRuleRepository);
    }

    private CharterRuleEntity rule() {
        return CharterRuleEntity.builder()
                .id(1L).teamId(1L).title("옛 제목").content("옛 내용")
                .status(CharterRuleStatus.DRAFT).build();
    }

    @Test
    void 관리자면_규칙을_수정한다() {
        when(charterRuleRepository.findByIdAndTeamId(1L, 1L)).thenReturn(Optional.of(rule()));

        CharterRuleEntity result = sut().update(1L, 10L, 1L, new UpdateCharterRuleCommand("새 제목", "새 내용"));

        assertThat(result.getTitle()).isEqualTo("새 제목");
        assertThat(result.getContent()).isEqualTo("새 내용");
    }

    @Test
    void 다른_팀_소속이거나_없는_규칙이면_예외() {
        when(charterRuleRepository.findByIdAndTeamId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().update(1L, 10L, 1L, new UpdateCharterRuleCommand("제목", "내용")))
                .isInstanceOf(CharterRuleNotFoundException.class);
    }

    @Test
    void 관리자가_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().update(1L, 99L, 1L, new UpdateCharterRuleCommand("제목", "내용")))
                .isInstanceOf(ForbiddenException.class);
    }
}
