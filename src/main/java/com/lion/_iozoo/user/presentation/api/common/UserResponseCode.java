package com.lion._iozoo.user.presentation.api.common;

import com.lion._iozoo.global.presentation.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserResponseCode implements ResponseCode {
    SIGNUP_SUCCESS("USER_201_1", "회원가입에 성공했습니다."),
    LOGIN_SUCCESS("USER_200_1", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS("USER_200_2", "로그아웃되었습니다."),
    PROFILE_UPDATED("USER_200_3", "프로필이 수정되었습니다.");

    private final String code;
    private final String message;
}
