package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ForbiddenException;

public class DocPrAccessDeniedException extends ForbiddenException {
    public DocPrAccessDeniedException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_ACCESS_DENIED, DocPrErrorCode.DOCPR_ACCESS_DENIED.getMessage());
        addContext("docPrId", docPrId);
    }
}
