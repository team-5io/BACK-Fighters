package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.result.DocPrReview;

import java.util.List;

public interface GetDocPrReviewsUseCase {
    List<DocPrReview> getReviews(Long userId, Long docPrId);
}
