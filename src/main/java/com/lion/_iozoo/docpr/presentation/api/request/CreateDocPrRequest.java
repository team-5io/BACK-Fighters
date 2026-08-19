package com.lion._iozoo.docpr.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDocPrRequest(
        @Schema(description = "승인권자(A) 팀원 ID (팀원 목록 조회 응답의 memberId)", example = "2")
        @NotNull(message = "승인권자 ID는 필수입니다.")
        Long approverMemberId,

        @Schema(description = "이 Doc PR이 제안하는 변경 내용", example = "3장 배포 절차를 최신 CI 스크립트 기준으로 수정")
        @NotBlank(message = "제안 내용은 필수입니다.")
        String proposedContent
) {
}
