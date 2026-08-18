package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.AiReview;

import java.util.Optional;

public interface LoadAiReviewPort {
    Optional<AiReview> loadByDocPrId(Long docPrId);
}
