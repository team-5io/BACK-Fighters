package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.AiReviewIssue;

import java.util.List;

public interface GetAiReviewIssuesUseCase {
    List<AiReviewIssue> getUnresolvedIssues(Long userId, Long docPrId);
}
