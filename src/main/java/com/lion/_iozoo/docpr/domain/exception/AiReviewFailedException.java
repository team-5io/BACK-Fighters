package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class AiReviewFailedException extends BusinessException {
    public AiReviewFailedException(Long docPrId, Throwable cause) {
        super(DocPrErrorCode.AI_REVIEW_FAILED, DocPrErrorCode.AI_REVIEW_FAILED.getMessage(), cause);
        addContext("docPrId", docPrId);
    }
}
