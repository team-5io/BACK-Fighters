package com.lion._iozoo.user.presentation.api.response;

import com.lion._iozoo.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateProfileResponse(
        @Schema(description = "외부 노출용 유저 ID", example = "3f2e4b1a-1234-4a5b-9c6d-abcdef123456")
        UUID publicId,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "김재원")
        String name,

        @Schema(description = "시간대", example = "Asia/Seoul")
        String timezone,

        @Schema(description = "선호 언어", example = "ko")
        String language
) {
    public static UpdateProfileResponse from(User user) {
        return UpdateProfileResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .name(user.getName())
                .timezone(user.getTimezone())
                .language(user.getLanguage())
                .build();
    }
}