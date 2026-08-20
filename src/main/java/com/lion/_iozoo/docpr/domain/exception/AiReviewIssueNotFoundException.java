package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class AiReviewIssueNotFoundException extends NotFoundException {
    public AiReviewIssueNotFoundException(Long issueId) {
        super(DocPrErrorCode.AI_REVIEW_ISSUE_NOT_FOUND, DocPrErrorCode.AI_REVIEW_ISSUE_NOT_FOUND.getMessage());
        addContext("issueId", issueId);
    }
}
