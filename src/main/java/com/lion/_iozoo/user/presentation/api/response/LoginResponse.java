package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponse(
        @Schema(description = "외부 노출용 유저 ID", example = "3f2e4b1a-1234-4a5b-9c6d-abcdef123456")
        UUID publicId,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "김재원")
        String name,

        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken
) {
    public static LoginResponse of(User user, String accessToken) {
        return LoginResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .build();
    }
}