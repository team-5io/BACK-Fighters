package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;

import java.util.List;

public interface ListTeamMembersUseCase {
    List<TeamMemberEntity> listMembers(Long teamId, Long requesterId);
}
