package com.lion._iozoo.document.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SetDocumentRaciRequest(
        @Schema(description = "문서에 배정할 RACI 목록 (기존 배정을 전체 교체함)")
        @NotNull(message = "배정 목록은 필수입니다.")
        @Valid
        List<RaciAssignmentRequest> assignments
) {
}
