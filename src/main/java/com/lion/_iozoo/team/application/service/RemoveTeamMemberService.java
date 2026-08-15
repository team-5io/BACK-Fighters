package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveTeamMemberService implements RemoveTeamMemberUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public void remove(Long teamId, Long requesterId, Long targetUserId) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, targetUserId)) {
            throw new TeamMemberNotFoundException();
        }

        if (!requesterId.equals(targetUserId)) {
            teamPermissionChecker.requireAdmin(teamId, requesterId);
        }

        teamMemberRepository.deleteByTeamIdAndUserId(teamId, targetUserId);
    }
}
