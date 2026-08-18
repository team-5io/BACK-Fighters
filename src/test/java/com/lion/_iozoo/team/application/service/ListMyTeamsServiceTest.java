package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.result.MyTeamResult;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import com.lion._iozoo.team.infrastructure.persistence.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListMyTeamsServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamRepository teamRepository;

    private ListMyTeamsService sut() {
        return new ListMyTeamsService(teamMemberRepository, teamRepository);
    }

    @Test
    void 내가_속한_팀_목록을_역할과_함께_조회한다() {
        TeamMemberEntity membership = TeamMemberEntity.builder()
                .teamId(1L).userId(10L).role(TeamRole.MEMBER).joinedAt(LocalDateTime.now())
                .build();
        when(teamMemberRepository.findAllByUserId(10L)).thenReturn(List.of(membership));
        when(teamRepository.findAllById(Set.of(1L)))
                .thenReturn(List.of(TeamEntity.builder().id(1L).name("5조 파이터즈").build()));

        List<MyTeamResult> result = sut().listMyTeams(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).team().getName()).isEqualTo("5조 파이터즈");
        assertThat(result.get(0).role()).isEqualTo(TeamRole.MEMBER);
    }
}
