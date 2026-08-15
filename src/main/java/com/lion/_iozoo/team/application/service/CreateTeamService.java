package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.command.CreateTeamCommand;
import com.lion._iozoo.team.application.usecase.CreateTeamUseCase;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import com.lion._iozoo.team.infrastructure.persistence.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateTeamService implements CreateTeamUseCase {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public TeamEntity createTeam(Long creatorUserId, CreateTeamCommand command) {
        // 1. 팀 생성
        TeamEntity team = teamRepository.save(
                TeamEntity.builder().name(command.name()).build()
        );

        // 2. 생성자를 해당 팀의 ADMIN으로 자동 등록
        teamMemberRepository.save(
                TeamMemberEntity.builder()
                        .teamId(team.getId())
                        .userId(creatorUserId)
                        .role(TeamRole.ADMIN)
                        .joinedAt(LocalDateTime.now())
                        .build()
        );

        return team;
    }
}
