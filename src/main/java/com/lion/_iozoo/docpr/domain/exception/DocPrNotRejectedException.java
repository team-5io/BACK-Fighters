package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class DocPrNotRejectedException extends ConflictException {
    public DocPrNotRejectedException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_NOT_REJECTED);
        addContext("docPrId", docPrId);
    }
}
