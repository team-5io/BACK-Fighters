package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.AiReviewIssue;

public interface ResolveAiReviewIssueUseCase {
    AiReviewIssue resolve(Long userId, Long docPrId, Long issueId);
}
