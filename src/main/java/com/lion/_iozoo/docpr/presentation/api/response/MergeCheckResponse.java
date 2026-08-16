package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.application.result.MergeCheckResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MergeCheckResponse(
        @Schema(description = "병합 가능 여부", example = "true")
        boolean mergeable,

        @Schema(description = "병합 불가 사유 (mergeable=true면 null)", example = "승인된 Doc PR만 병합할 수 있습니다.")
        String reason
) {
    public static MergeCheckResponse from(MergeCheckResult result) {
        return MergeCheckResponse.builder()
                .mergeable(result.mergeable())
                .reason(result.reason())
                .build();
    }
}
