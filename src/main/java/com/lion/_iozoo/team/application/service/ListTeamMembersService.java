package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.usecase.ListTeamMembersUseCase;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListTeamMembersService implements ListTeamMembersUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberEntity> listMembers(Long teamId, Long requesterId) {
        log.info("event=team_member_list_시작 teamId={}, requesterId={}", teamId, requesterId);

        try {
            teamPermissionChecker.requireMember(teamId, requesterId);

            List<TeamMemberEntity> members = teamMemberRepository.findAllByTeamId(teamId);

            log.info("event=team_member_list_완료 teamId={}, requesterId={}, count={}",
                    teamId, requesterId, members.size());
            return members;
        } catch (RuntimeException e) {
            log.warn("event=team_member_list_실패 teamId={}, requesterId={}, reason={}",
                    teamId, requesterId, e.getMessage(), e);
            throw e;
        }
    }
}
