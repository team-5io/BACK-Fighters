package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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

    private TeamMemberEntity member(Long memberId, Long teamId, Long userId, TeamRole role) {
        return TeamMemberEntity.builder()
                .id(memberId).teamId(teamId).userId(userId).role(role).joinedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void ADMIN이_다른_팀원을_추방한다() {
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(member(2L, 1L, 20L, TeamRole.MEMBER)));

        sut().remove(1L, 10L, 2L);

        verify(teamPermissionChecker).requireAdmin(1L, 10L);
        verify(teamMemberRepository).deleteById(2L);
    }

    @Test
    void 본인이_스스로_탈퇴한다() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(member(1L, 1L, 10L, TeamRole.ADMIN)));

        sut().remove(1L, 10L, 1L);

        verify(teamPermissionChecker, never()).requireAdmin(1L, 10L);
        verify(teamMemberRepository).deleteById(1L);
    }

    @Test
    void ADMIN이_아닌_사람이_다른_팀원을_추방하면_예외() {
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(member(2L, 1L, 20L, TeamRole.MEMBER)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 11L);

        assertThatThrownBy(() -> sut().remove(1L, 11L, 2L))
                .isInstanceOf(ForbiddenException.class);

        verify(teamMemberRepository, never()).deleteById(2L);
    }

    @Test
    void 대상_memberId가_없으면_예외() {
        when(teamMemberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().remove(1L, 10L, 99L))
                .isInstanceOf(TeamMemberNotFoundException.class);

        verify(teamMemberRepository, never()).deleteById(99L);
    }

    @Test
    void 대상_memberId가_다른_팀_소속이면_예외() {
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(member(2L, 999L, 20L, TeamRole.MEMBER)));

        assertThatThrownBy(() -> sut().remove(1L, 10L, 2L))
                .isInstanceOf(TeamMemberNotFoundException.class);

        verify(teamMemberRepository, never()).deleteById(2L);
    }
}
