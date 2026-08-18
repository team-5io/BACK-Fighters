package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.AiReview;

public interface RequestAiReviewUseCase {
    AiReview request(Long userId, Long docPrId);
}
