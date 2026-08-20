package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.AiReviewIssue;

import java.util.List;
import java.util.Optional;

public interface LoadAiReviewIssuesPort {
    List<AiReviewIssue> loadByDocPrId(Long docPrId);
    List<AiReviewIssue> loadUnresolvedByDocPrId(Long docPrId);
    Optional<AiReviewIssue> loadById(Long issueId);
}
