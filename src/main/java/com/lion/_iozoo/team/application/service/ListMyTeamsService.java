package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.result.MyTeamResult;
import com.lion._iozoo.team.application.usecase.ListMyTeamsUseCase;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import com.lion._iozoo.team.infrastructure.persistence.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMyTeamsService implements ListMyTeamsUseCase {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MyTeamResult> listMyTeams(Long userId) {
        List<TeamMemberEntity> memberships = teamMemberRepository.findAllByUserId(userId);
        Map<Long, TeamRole> roleByTeamId = memberships.stream()
                .collect(Collectors.toMap(TeamMemberEntity::getTeamId, TeamMemberEntity::getRole));

        List<TeamEntity> teams = teamRepository.findAllById(roleByTeamId.keySet());

        return teams.stream()
                .map(team -> new MyTeamResult(team, roleByTeamId.get(team.getId())))
                .toList();
    }
}
