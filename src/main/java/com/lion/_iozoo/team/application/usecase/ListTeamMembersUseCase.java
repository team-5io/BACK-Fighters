package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.result.TeamMemberResult;

import java.util.List;

public interface ListTeamMembersUseCase {
    List<TeamMemberResult> listMembers(Long teamId, Long requesterId);
}
