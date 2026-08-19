package com.lion._iozoo.team.application;

import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
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

    /**
     * teamId 소속 memberId(team_members PK)를 실제 userId로 변환한다.
     * memberId가 없거나 teamId 소속이 아니면 TeamMemberNotFoundException을 던진다.
     * 이 조회 자체가 팀 소속 여부 검증을 겸한다.
     */
    public Long resolveUserId(Long teamId, Long memberId) {
        return teamMemberRepository.findById(memberId)
                .filter(member -> member.getTeamId().equals(teamId))
                .map(TeamMemberEntity::getUserId)
                .orElseThrow(TeamMemberNotFoundException::new);
    }
}
