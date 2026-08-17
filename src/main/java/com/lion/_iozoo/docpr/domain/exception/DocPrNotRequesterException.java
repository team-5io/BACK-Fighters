package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ForbiddenException;

public class DocPrNotRequesterException extends ForbiddenException {
    public DocPrNotRequesterException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_NOT_REQUESTER, DocPrErrorCode.DOCPR_NOT_REQUESTER.getMessage());
        addContext("docPrId", docPrId);
    }
}
