package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.application.result.DocPrReview;

import java.util.List;

public interface LoadDocPrReviewsPort {
    List<DocPrReview> loadByDocPrId(Long docPrId);
}
