package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class DocPrNotFoundException extends NotFoundException {
    public DocPrNotFoundException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_NOT_FOUND);
        addContext("docPrId", docPrId);
    }
}
