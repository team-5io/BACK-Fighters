package com.lion._iozoo.docpr.domain;

import com.lion._iozoo.docpr.domain.exception.AiReviewIssueAlreadyProcessedException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiReviewIssue {
    private final Long id;
    private final Long docPrId;
    private final String severity;
    private final String issueType;
    private final String description;
    private final Long relatedDocumentId;
    private final String charterRuleId;
    private final String blockId;
    private final String quote;
    private AiReviewIssueStatus status;
    private final LocalDateTime createdAt;

    @Builder
    private AiReviewIssue(Long id, Long docPrId, String severity, String issueType, String description,
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

    public void resolve() {
        requireUnresolved();
        this.status = AiReviewIssueStatus.RESOLVED;
    }

    public void skip() {
        requireUnresolved();
        this.status = AiReviewIssueStatus.SKIPPED;
    }

    private void requireUnresolved() {
        if (status != AiReviewIssueStatus.UNRESOLVED) {
            throw new AiReviewIssueAlreadyProcessedException(id);
        }
    }
}
