package com.lion._iozoo.document.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDocumentRequest(
        @NotNull(message = "팀 ID는 필수입니다.")
        Long teamId,

        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String content
) {
}
