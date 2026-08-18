package com.lion._iozoo.team.application.service;

import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.port.out.LoadUserIdByEmailPort;
import com.lion._iozoo.team.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.team.application.port.out.UserSummary;
import com.lion._iozoo.team.application.result.TeamMemberResult;
import com.lion._iozoo.team.application.usecase.InviteTeamMemberUseCase;
import com.lion._iozoo.team.domain.TeamRole;
import com.lion._iozoo.team.domain.exception.AlreadyTeamMemberException;
import com.lion._iozoo.team.domain.exception.InvitedUserNotFoundException;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteTeamMemberService implements InviteTeamMemberUseCase {

    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadUserIdByEmailPort loadUserIdByEmailPort;
    private final LoadUserSummaryPort loadUserSummaryPort;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public TeamMemberResult invite(Long teamId, Long inviterId, InviteTeamMemberCommand command) {
        log.info("event=team_member_invite_시작 teamId={}, inviterId={}", teamId, inviterId);

        try {
            // 1. 초대자가 이 팀의 ADMIN인지 확인
            teamPermissionChecker.requireAdmin(teamId, inviterId);

            // 2. 초대할 이메일이 가입된 유저인지 확인
            Long invitedUserId = loadUserIdByEmailPort.loadUserIdByEmail(command.email())
                    .orElseThrow(InvitedUserNotFoundException::new);

            // 3. 이미 팀원이면 중복 초대 방지
            if (teamMemberRepository.existsByTeamIdAndUserId(teamId, invitedUserId)) {
                throw new AlreadyTeamMemberException();
            }

            // 4. MEMBER로 등록
            TeamMemberEntity saved = teamMemberRepository.save(
                    TeamMemberEntity.builder()
                            .teamId(teamId)
                            .userId(invitedUserId)
                            .role(TeamRole.MEMBER)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );

            UserSummary summary = loadUserSummaryPort.loadSummariesByUserIds(List.of(invitedUserId)).get(invitedUserId);

            log.info("event=team_member_invite_완료 teamId={}, inviterId={}, invitedUserId={}",
                    teamId, inviterId, invitedUserId);
            return new TeamMemberResult(
                    saved.getId(), saved.getRole(), saved.getJoinedAt(),
                    summary == null ? null : summary.name(),
                    summary == null ? null : summary.email());
        } catch (RuntimeException e) {
            log.warn("event=team_member_invite_실패 teamId={}, inviterId={}, reason={}",
                    teamId, inviterId, e.getMessage(), e);
            throw e;
        }
    }
}
