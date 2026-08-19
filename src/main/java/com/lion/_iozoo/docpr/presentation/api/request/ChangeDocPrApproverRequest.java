package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangeDocPrApproverRequest(
        @Schema(description = "새 승인권자(A) 팀원 ID (팀원 목록 조회 응답의 memberId)", example = "3")
        @NotNull(message = "새 승인권자 ID는 필수입니다.")
        Long newApproverMemberId
) {
}
