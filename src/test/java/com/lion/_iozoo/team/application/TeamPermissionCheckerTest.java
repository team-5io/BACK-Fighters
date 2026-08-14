package com.lion._iozoo.team.application;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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
}
