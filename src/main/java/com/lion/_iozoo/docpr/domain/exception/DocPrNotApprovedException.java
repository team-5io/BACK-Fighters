package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class DocPrNotApprovedException extends ConflictException {
    public DocPrNotApprovedException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_NOT_APPROVED);
        addContext("docPrId", docPrId);
    }
}
