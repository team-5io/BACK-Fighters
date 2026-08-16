package com.lion._iozoo.document.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDocumentRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String content
) {
}
