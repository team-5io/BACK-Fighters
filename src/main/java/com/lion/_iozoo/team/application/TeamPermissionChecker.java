package com.lion._iozoo.team.application;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamPermissionChecker {

    private final TeamMemberRepository teamMemberRepository;

    /**
     * userId가 teamId의 ADMIN이 아니면 ForbiddenException을 던진다.
     */
    public void requireAdmin(Long teamId, Long userId) {
        TeamMemberEntity member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(ForbiddenException::new);

        if (member.getRole() != TeamRole.ADMIN) {
            throw new ForbiddenException();
        }
    }

    /**
     * userId가 teamId에 소속된 팀원(MEMBER/ADMIN 무관)이 아니면 ForbiddenException을 던진다.
     */
    public void requireMember(Long teamId, Long userId) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new ForbiddenException();
        }
    }
}
