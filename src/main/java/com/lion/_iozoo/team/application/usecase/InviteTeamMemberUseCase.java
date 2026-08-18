package com.lion._iozoo.team.application.usecase;

import com.lion._iozoo.team.application.command.InviteTeamMemberCommand;
import com.lion._iozoo.team.application.result.TeamMemberResult;

public interface InviteTeamMemberUseCase {
    TeamMemberResult invite(Long teamId, Long inviterId, InviteTeamMemberCommand command);
}
