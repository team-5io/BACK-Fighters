package com.lion._iozoo.team.domain.exception;

import com.lion._iozoo.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamErrorCode implements ErrorCode {
    INVITED_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_404_1", "가입되지 않은 이메일입니다."),
    ALREADY_TEAM_MEMBER(HttpStatus.CONFLICT, "TEAM_409_1", "이미 팀에 소속된 유저입니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_404_2", "팀을 찾을 수 없습니다."),
    TEAM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_404_3", "팀에 소속되지 않은 유저입니다."),
    CHARTER_DRAFT_FAILED(HttpStatus.BAD_GATEWAY, "TEAM_502_1", "협업 규칙 초안 생성이 실패했습니다."),
    CHARTER_RULE_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_404_4", "협업 규칙을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
