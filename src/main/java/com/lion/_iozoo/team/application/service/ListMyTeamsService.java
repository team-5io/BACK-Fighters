package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.usecase.ListMyTeamsUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import com.lion._iozoo.team.infrastructure.persistence.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListMyTeamsService implements ListMyTeamsUseCase {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TeamEntity> listMyTeams(Long userId) {
        List<Long> teamIds = teamMemberRepository.findAllByUserId(userId)
                .stream()
                .map(TeamMemberEntity::getTeamId)
                .toList();

        return teamRepository.findAllById(teamIds);
    }
}
