package com.lion._iozoo.user.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @Schema(description = "변경할 이름", example = "김재원")
        @NotBlank(message = "변경할 이름은 필수입니다.")
        String name,

        @Schema(description = "시간대", example = "Asia/Seoul")
        String timezone,

        @Schema(description = "선호 언어", example = "ko")
        String language
) {
}