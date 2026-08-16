package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SignupResponse(
        @Schema(description = "외부 노출용 유저 ID", example = "3f2e4b1a-1234-4a5b-9c6d-abcdef123456")
        UUID publicId,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "김재원")
        String name
) {
    // 도메인 객체를 받아서 응답 DTO로 변환하는 메서드
    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}