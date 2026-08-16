package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveTeamMemberService implements RemoveTeamMemberUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public void remove(Long teamId, Long requesterId, Long targetUserId) {
        log.info("event=team_member_remove_시작 teamId={}, requesterId={}, targetUserId={}",
                teamId, requesterId, targetUserId);

        try {
            if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, targetUserId)) {
                throw new TeamMemberNotFoundException();
            }

            if (!requesterId.equals(targetUserId)) {
                teamPermissionChecker.requireAdmin(teamId, requesterId);
            }

            teamMemberRepository.deleteByTeamIdAndUserId(teamId, targetUserId);

            log.info("event=team_member_remove_완료 teamId={}, requesterId={}, targetUserId={}",
                    teamId, requesterId, targetUserId);
        } catch (RuntimeException e) {
            log.warn("event=team_member_remove_실패 teamId={}, requesterId={}, targetUserId={}, reason={}",
                    teamId, requesterId, targetUserId, e.getMessage(), e);
            throw e;
        }
    }
}
