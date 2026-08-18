package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.application.result.NextAssigneeInfoResult;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NextAssigneeInfoResponse(
        @Schema(description = "Doc PR ID", example = "1")
        Long docPrId,

        @Schema(description = "현재 Doc PR 상태", example = "REVIEWER_NEEDED")
        DocPrStatus status,

        @Schema(description = "다음 작업자 지정이 필요한 상태인지 여부 (status == REVIEWER_NEEDED)", example = "true")
        boolean needsNextAssignee,

        @Schema(description = "지정된 다음 작업자 ID (아직 지정 안 됐으면 null)", example = "null")
        Long nextAssigneeId,

        @Schema(description = "인수인계 정보 위치 — 가장 최근 상태 전이 이력 (이력이 없으면 null)")
        DocPrHistoryResponse latestHandoff
) {
    public static NextAssigneeInfoResponse from(NextAssigneeInfoResult result) {
        return NextAssigneeInfoResponse.builder()
                .docPrId(result.docPrId())
                .status(result.status())
                .needsNextAssignee(result.needsNextAssignee())
                .nextAssigneeId(result.nextAssigneeId())
                .latestHandoff(result.latestHandoff() == null ? null : DocPrHistoryResponse.from(result.latestHandoff()))
                .build();
    }
}
