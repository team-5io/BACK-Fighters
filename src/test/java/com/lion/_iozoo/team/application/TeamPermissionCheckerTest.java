package com.lion._iozoo.team.application;

import com.lion._iozoo.global.exception.ForbiddenException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamPermissionCheckerTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private TeamPermissionChecker sut;

    @Test
    void ADMIN이면_통과한다() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        TeamMemberEntity admin = TeamMemberEntity.builder()
                .teamId(1L).userId(10L).role(TeamRole.ADMIN).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findByTeamIdAndUserId(1L, 10L)).thenReturn(Optional.of(admin));

        assertThatCode(() -> sut.requireAdmin(1L, 10L)).doesNotThrowAnyException();
    }

    @Test
    void MEMBER이면_ForbiddenException() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        TeamMemberEntity member = TeamMemberEntity.builder()
                .teamId(1L).userId(11L).role(TeamRole.MEMBER).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findByTeamIdAndUserId(1L, 11L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> sut.requireAdmin(1L, 11L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 팀_소속이_아니면_ForbiddenException() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        when(teamMemberRepository.findByTeamIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.requireAdmin(1L, 99L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 팀원이면_requireMember_통과한다() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 11L)).thenReturn(true);

        assertThatCode(() -> sut.requireMember(1L, 11L)).doesNotThrowAnyException();
    }

    @Test
    void 팀_소속이_아니면_requireMember_ForbiddenException() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 99L)).thenReturn(false);

        assertThatThrownBy(() -> sut.requireMember(1L, 99L)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void memberId로_userId를_조회한다() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        TeamMemberEntity member = TeamMemberEntity.builder()
                .id(5L).teamId(1L).userId(20L).role(TeamRole.MEMBER).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findById(5L)).thenReturn(Optional.of(member));

        assertThat(sut.resolveUserId(1L, 5L)).isEqualTo(20L);
    }

    @Test
    void memberId가_다른_팀_소속이면_TeamMemberNotFoundException() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        TeamMemberEntity member = TeamMemberEntity.builder()
                .id(5L).teamId(2L).userId(20L).role(TeamRole.MEMBER).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findById(5L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> sut.resolveUserId(1L, 5L)).isInstanceOf(TeamMemberNotFoundException.class);
    }

    @Test
    void memberId가_존재하지_않으면_TeamMemberNotFoundException() {
        sut = new TeamPermissionChecker(teamMemberRepository);
        when(teamMemberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.resolveUserId(1L, 99L)).isInstanceOf(TeamMemberNotFoundException.class);
    }
}
