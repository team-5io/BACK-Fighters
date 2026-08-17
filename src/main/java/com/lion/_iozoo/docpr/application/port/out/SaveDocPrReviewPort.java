package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.application.result.DocPrReview;

public interface SaveDocPrReviewPort {
    DocPrReview save(Long docPrId, Long reviewerId, String comment);
}
