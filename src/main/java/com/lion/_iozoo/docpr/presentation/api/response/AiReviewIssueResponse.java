package com.lion._iozoo.docpr.presentation.api.response;

import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AiReviewIssueResponse(
        @Schema(description = "이슈 ID", example = "1")
        Long id,

        @Schema(description = "Doc PR ID", example = "7")
        Long docPrId,

        @Schema(description = "심각도", example = "critical")
        String severity,

        @Schema(description = "이슈 종류", example = "conflict")
        String issueType,

        @Schema(description = "이슈 설명 (영어)", example = "string")
        String description,

        @Schema(description = "상충/비일관 대상 문서 ID", example = "200")
        Long relatedDocumentId,

        @Schema(description = "위반한 협업 규칙 ID")
        String charterRuleId,

        @Schema(description = "이슈 위치 블록 id (blocks 없이 검토했으면 null)")
        String blockId,

        @Schema(description = "문제 문장의 원문 발췌")
        String quote,

        @Schema(description = "이슈 처리 상태", example = "UNRESOLVED")
        AiReviewIssueStatus status,

        @Schema(description = "이슈 생성 시각")
        LocalDateTime createdAt
) {
    public static AiReviewIssueResponse from(AiReviewIssue issue) {
        return AiReviewIssueResponse.builder()
                .id(issue.getId())
                .docPrId(issue.getDocPrId())
                .severity(issue.getSeverity())
                .issueType(issue.getIssueType())
                .description(issue.getDescription())
                .relatedDocumentId(issue.getRelatedDocumentId())
                .charterRuleId(issue.getCharterRuleId())
                .blockId(issue.getBlockId())
                .quote(issue.getQuote())
                .status(issue.getStatus())
                .createdAt(issue.getCreatedAt())
                .build();
    }
}
