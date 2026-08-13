package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import lombok.Builder;

@Builder
public record SignupResponse(
        Long id,
        String email,
        String name
) {
    // 도메인 객체를 받아서 응답 DTO로 변환하는 메서드
    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}