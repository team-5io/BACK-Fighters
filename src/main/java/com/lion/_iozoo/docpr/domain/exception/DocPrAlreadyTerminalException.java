package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ConflictException;

public class DocPrAlreadyTerminalException extends ConflictException {
    public DocPrAlreadyTerminalException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_ALREADY_TERMINAL);
        addContext("docPrId", docPrId);
    }
}
