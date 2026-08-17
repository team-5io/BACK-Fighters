package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.infrastructure.persistence.TeamMemberEntity;

public interface InviteTeamMemberUseCase {
    TeamMemberEntity invite(Long teamId, Long inviterId, InviteTeamMemberCommand command);
}
