package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class TeamMemberNotFoundException extends NotFoundException {
    public TeamMemberNotFoundException() {
        super(TeamErrorCode.TEAM_MEMBER_NOT_FOUND);
    }
}
