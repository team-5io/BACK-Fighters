package com.lion._iozoo.docpr.application.result;

public record AiReviewIssueResult(
        String severity,
        String issueType,
        String description,
        Long relatedDocumentId,
        String charterRuleId,
        String blockId,
        String quote) {
}
