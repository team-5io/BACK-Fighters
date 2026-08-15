package com.lion._iozoo.team.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamResponseCode implements ResponseCode {
    TEAM_CREATED("TEAM_201_1", "팀이 생성되었습니다.");

    private final String code;
    private final String message;
}
