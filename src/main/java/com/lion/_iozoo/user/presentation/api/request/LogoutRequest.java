package com.lion._iozoo.user.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @Schema(description = "무효화할 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        @NotBlank(message = "액세스 토큰은 필수입니다.")
        String accessToken
) {
}