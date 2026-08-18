package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.port.out.LoadUserIdByEmailPort;
import com.lion._iozoo.team.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.team.application.port.out.UserSummary;
import com.lion._iozoo.team.application.result.TeamMemberResult;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.domain.exception.AlreadyTeamMemberException;
import com.lion._iozoo.team.domain.exception.InvitedUserNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteTeamMemberServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private LoadUserIdByEmailPort loadUserIdByEmailPort;
    @Mock
    private LoadUserSummaryPort loadUserSummaryPort;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private InviteTeamMemberService sut() {
        return new InviteTeamMemberService(teamPermissionChecker, loadUserIdByEmailPort, loadUserSummaryPort, teamMemberRepository);
    }

    @Test
    void 정상_초대시_MEMBER로_등록된다() {
        when(loadUserIdByEmailPort.loadUserIdByEmail("a@b.com")).thenReturn(Optional.of(20L));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 20L)).thenReturn(false);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenAnswer(invocation -> {
            TeamMemberEntity arg = invocation.getArgument(0);
            return TeamMemberEntity.builder()
                    .id(1L).teamId(arg.getTeamId()).userId(arg.getUserId())
                    .role(arg.getRole()).joinedAt(arg.getJoinedAt())
                    .build();
        });
        when(loadUserSummaryPort.loadSummariesByUserIds(List.of(20L)))
                .thenReturn(Map.of(20L, new UserSummary("피초대자", "a@b.com")));

        TeamMemberResult result = sut().invite(1L, 10L, new InviteTeamMemberCommand("a@b.com"));

        assertThat(result.role()).isEqualTo(TeamRole.MEMBER);
        assertThat(result.name()).isEqualTo("피초대자");
        assertThat(result.email()).isEqualTo("a@b.com");
        verify(teamPermissionChecker).requireAdmin(1L, 10L);
    }

    @Test
    void 가입되지_않은_이메일이면_예외() {
        when(loadUserIdByEmailPort.loadUserIdByEmail("nobody@b.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().invite(1L, 10L, new InviteTeamMemberCommand("nobody@b.com")))
                .isInstanceOf(InvitedUserNotFoundException.class);

        verify(teamMemberRepository, never()).save(any(TeamMemberEntity.class));
    }

    @Test
    void 이미_팀원이면_예외() {
        when(loadUserIdByEmailPort.loadUserIdByEmail("a@b.com")).thenReturn(Optional.of(20L));
        when(teamMemberRepository.existsByTeamIdAndUserId(1L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> sut().invite(1L, 10L, new InviteTeamMemberCommand("a@b.com")))
                .isInstanceOf(AlreadyTeamMemberException.class);

        verify(teamMemberRepository, never()).save(any(TeamMemberEntity.class));
    }
}
