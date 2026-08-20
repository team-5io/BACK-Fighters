package com.lion._iozoo.docpr.infrastructure.persistence.entity;

import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_review_issues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiReviewIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_pr_id", nullable = false)
    private Long docPrId;

    @Column(nullable = false)
    private String severity;

    @Column(name = "issue_type", nullable = false)
    private String issueType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "related_document_id")
    private Long relatedDocumentId;

    @Column(name = "charter_rule_id")
    private String charterRuleId;

    @Column(name = "block_id")
    private String blockId;

    @Column(columnDefinition = "TEXT")
    private String quote;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiReviewIssueStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private AiReviewIssueEntity(Long id, Long docPrId, String severity, String issueType, String description,
                                 Long relatedDocumentId, String charterRuleId, String blockId, String quote,
                                 AiReviewIssueStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.docPrId = docPrId;
        this.severity = severity;
        this.issueType = issueType;
        this.description = description;
        this.relatedDocumentId = relatedDocumentId;
        this.charterRuleId = charterRuleId;
        this.blockId = blockId;
        this.quote = quote;
        this.status = status;
        this.createdAt = createdAt;
    }
}
