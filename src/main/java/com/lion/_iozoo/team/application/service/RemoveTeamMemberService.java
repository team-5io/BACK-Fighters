package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.RemoveTeamMemberUseCase;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
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
    public void remove(Long teamId, Long requesterId, Long memberId) {
        log.info("event=team_member_remove_시작 teamId={}, requesterId={}, memberId={}",
                teamId, requesterId, memberId);

        try {
            TeamMemberEntity member = teamMemberRepository.findById(memberId)
                    .filter(m -> m.getTeamId().equals(teamId))
                    .orElseThrow(TeamMemberNotFoundException::new);

            Long targetUserId = member.getUserId();
            if (!requesterId.equals(targetUserId)) {
                teamPermissionChecker.requireAdmin(teamId, requesterId);
            }

            teamMemberRepository.deleteById(memberId);

            log.info("event=team_member_remove_완료 teamId={}, requesterId={}, memberId={}",
                    teamId, requesterId, memberId);
        } catch (RuntimeException e) {
            log.warn("event=team_member_remove_실패 teamId={}, requesterId={}, memberId={}, reason={}",
                    teamId, requesterId, memberId, e.getMessage(), e);
            throw e;
        }
    }
}
