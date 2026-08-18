package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class AiReviewNotFoundException extends NotFoundException {
    public AiReviewNotFoundException(Long docPrId) {
        super(DocPrErrorCode.AI_REVIEW_NOT_FOUND, DocPrErrorCode.AI_REVIEW_NOT_FOUND.getMessage());
        addContext("docPrId", docPrId);
    }
}
