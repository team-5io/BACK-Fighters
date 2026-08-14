package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListTeamMembersService implements ListTeamMembersUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberEntity> listMembers(Long teamId, Long requesterId) {
        teamPermissionChecker.requireMember(teamId, requesterId);

        return teamMemberRepository.findAllByTeamId(teamId);
    }
}
