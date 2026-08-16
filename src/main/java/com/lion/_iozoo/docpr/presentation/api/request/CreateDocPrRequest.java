package com.lion._iozoo.docpr.presentation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDocPrRequest(
        @NotNull(message = "승인권자 ID는 필수입니다.")
        Long approverId,

        @NotBlank(message = "제안 내용은 필수입니다.")
        String proposedContent
) {
}
