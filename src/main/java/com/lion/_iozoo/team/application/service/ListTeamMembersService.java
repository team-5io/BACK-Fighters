package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.team.application.port.out.UserSummary;
import com.lion._iozoo.team.application.result.TeamMemberResult;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListTeamMembersService implements ListTeamMembersUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final TeamMemberRepository teamMemberRepository;
    private final LoadUserSummaryPort loadUserSummaryPort;

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResult> listMembers(Long teamId, Long requesterId) {
        log.info("event=team_member_list_시작 teamId={}, requesterId={}", teamId, requesterId);

        try {
            teamPermissionChecker.requireMember(teamId, requesterId);

            List<TeamMemberEntity> members = teamMemberRepository.findAllByTeamId(teamId);
            Map<Long, UserSummary> summaries = loadUserSummaryPort.loadSummariesByUserIds(
                    members.stream().map(TeamMemberEntity::getUserId).toList());

            List<TeamMemberResult> results = members.stream()
                    .map(member -> toResult(member, summaries.get(member.getUserId())))
                    .toList();

            log.info("event=team_member_list_완료 teamId={}, requesterId={}, count={}",
                    teamId, requesterId, results.size());
            return results;
        } catch (RuntimeException e) {
            log.warn("event=team_member_list_실패 teamId={}, requesterId={}, reason={}",
                    teamId, requesterId, e.getMessage(), e);
            throw e;
        }
    }

    private TeamMemberResult toResult(TeamMemberEntity member, UserSummary summary) {
        return new TeamMemberResult(
                member.getId(),
                member.getRole(),
                member.getJoinedAt(),
                summary == null ? null : summary.name(),
                summary == null ? null : summary.email());
    }
}
