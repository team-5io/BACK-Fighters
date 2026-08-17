package com.lion._iozoo.document.presentation.api.request;

import com.lion._iozoo.document.domain.RaciRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RaciAssignmentRequest(
        @Schema(description = "배정할 사용자 ID", example = "3")
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @Schema(description = "RACI 역할", example = "R")
        @NotNull(message = "역할은 필수입니다.")
        RaciRole role
) {
}
