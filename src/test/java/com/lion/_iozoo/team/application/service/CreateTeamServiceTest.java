package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import com.lion._iozoo.team.infrastructure.persistence.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTeamServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Test
    void 팀_생성시_생성자가_ADMIN으로_등록된다() {
        CreateTeamService sut = new CreateTeamService(teamRepository, teamMemberRepository);
        TeamEntity savedTeam = TeamEntity.builder().id(1L).name("Doc PR 팀").build();
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(savedTeam);

        TeamEntity result = sut.createTeam(10L, new CreateTeamCommand("Doc PR 팀"));

        assertThat(result.getId()).isEqualTo(1L);

        ArgumentCaptor<TeamMemberEntity> captor = ArgumentCaptor.forClass(TeamMemberEntity.class);
        verify(teamMemberRepository).save(captor.capture());
        assertThat(captor.getValue().getTeamId()).isEqualTo(1L);
        assertThat(captor.getValue().getUserId()).isEqualTo(10L);
        assertThat(captor.getValue().getRole()).isEqualTo(TeamRole.ADMIN);
    }
}
