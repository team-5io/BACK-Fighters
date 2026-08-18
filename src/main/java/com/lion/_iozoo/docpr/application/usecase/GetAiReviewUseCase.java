package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.AiReview;

public interface GetAiReviewUseCase {
    AiReview getByDocPrId(Long userId, Long docPrId);
}
