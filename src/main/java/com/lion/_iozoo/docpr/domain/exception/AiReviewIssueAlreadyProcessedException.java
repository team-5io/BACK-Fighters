package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class AiReviewIssueAlreadyProcessedException extends BusinessException {
    public AiReviewIssueAlreadyProcessedException(Long issueId) {
        super(DocPrErrorCode.AI_REVIEW_ISSUE_ALREADY_PROCESSED, DocPrErrorCode.AI_REVIEW_ISSUE_ALREADY_PROCESSED.getMessage());
        addContext("issueId", issueId);
    }
}
