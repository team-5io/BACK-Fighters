package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveTeamMemberServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private RemoveTeamMemberService sut() {
        return new RemoveTeamMemberService(teamPermissionChecker, teamMemberRepository);
    }

    @Test
    void ADMIN이_다른_팀원을_추방한다() {
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 20L)).thenReturn(true);

        sut().remove(1L, 10L, 20L);

        verify(teamPermissionChecker).requireAdmin(1L, 10L);
        verify(teamMemberRepository).deleteByTeamIdAndUserId(1L, 20L);
    }

    @Test
    void 본인이_스스로_탈퇴한다() {
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 10L)).thenReturn(true);

        sut().remove(1L, 10L, 10L);

        verify(teamPermissionChecker, never()).requireAdmin(1L, 10L);
        verify(teamMemberRepository).deleteByTeamIdAndUserId(1L, 10L);
    }

    @Test
    void ADMIN이_아닌_사람이_다른_팀원을_추방하면_예외() {
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 20L)).thenReturn(true);
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 11L);

        assertThatThrownBy(() -> sut().remove(1L, 11L, 20L))
                .isInstanceOf(ForbiddenException.class);

        verify(teamMemberRepository, never()).deleteByTeamIdAndUserId(1L, 20L);
    }

    @Test
    void 대상이_팀원이_아니면_예외() {
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 99L)).thenReturn(false);

        assertThatThrownBy(() -> sut().remove(1L, 10L, 99L))
                .isInstanceOf(TeamMemberNotFoundException.class);

        verify(teamMemberRepository, never()).deleteByTeamIdAndUserId(1L, 99L);
    }
}
