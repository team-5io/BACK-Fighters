package com.lion._iozoo.team.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamResponseCode implements ResponseCode {
    TEAM_CREATED("TEAM_201_1", "팀이 생성되었습니다."),
    TEAM_MEMBER_INVITED("TEAM_201_2", "팀원을 초대했습니다."),
    TEAM_MEMBER_REMOVED("TEAM_200_1", "팀원을 삭제했습니다."),
    TEAM_MEMBERS_FETCHED("TEAM_200_2", "팀원 목록을 조회했습니다."),
    COLLABORATION_RULE_UPSERTED("TEAM_200_3", "협업 규칙을 수정했습니다."),
    MY_TEAMS_FETCHED("TEAM_200_4", "소속된 팀 목록을 조회했습니다."),
    COLLABORATION_RULE_DRAFT_GENERATED("TEAM_200_5", "협업 규칙 초안을 생성했습니다.");

    private final String code;
    private final String message;
}
