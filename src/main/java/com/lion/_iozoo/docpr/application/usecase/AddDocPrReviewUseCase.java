package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.AddDocPrReviewCommand;
import com.lion._iozoo.docpr.application.result.DocPrReview;

public interface AddDocPrReviewUseCase {
    DocPrReview addReview(Long userId, AddDocPrReviewCommand command);
}
