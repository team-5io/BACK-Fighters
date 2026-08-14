package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class AlreadyTeamMemberException extends ConflictException {
    public AlreadyTeamMemberException() {
        super(TeamErrorCode.ALREADY_TEAM_MEMBER);
    }
}
