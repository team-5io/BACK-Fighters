package com.lion._iozoo.team.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamResponseCode implements ResponseCode {
    TEAM_CREATED("TEAM_201_1", "팀이 생성되었습니다."),
    TEAM_MEMBER_INVITED("TEAM_201_2", "팀원을 초대했습니다."),
    TEAM_MEMBER_REMOVED("TEAM_200_1", "팀원을 삭제했습니다.");

    private final String code;
    private final String message;
}
