package com.lion._iozoo.domain.user.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank(message = "변경할 이름은 필수입니다.")
        String name,

        String timezone,

        String language
) {
}