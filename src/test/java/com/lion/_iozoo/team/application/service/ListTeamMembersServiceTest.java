package com.lion._iozoo.team.application.service;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTeamMembersServiceTest {

    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private ListTeamMembersService sut() {
        return new ListTeamMembersService(teamPermissionChecker, teamMemberRepository);
    }

    @Test
    void 팀원이면_목록을_조회한다() {
        TeamMemberEntity admin = TeamMemberEntity.builder()
                .teamId(1L).userId(10L).role(TeamRole.ADMIN).joinedAt(LocalDateTime.now())
                .build();
        TeamMemberEntity member = TeamMemberEntity.builder()
                .teamId(1L).userId(20L).role(TeamRole.MEMBER).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findAllByTeamId(1L)).thenReturn(List.of(admin, member));

        List<TeamMemberEntity> result = sut().listMembers(1L, 10L);

        assertThat(result).hasSize(2);
    }

    @Test
    void 팀_소속이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().listMembers(1L, 99L))
                .isInstanceOf(ForbiddenException.class);
    }
}
