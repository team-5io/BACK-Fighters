package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.BadRequestException;

public class DocPrNotDraftException extends BadRequestException {
    public DocPrNotDraftException(Long documentId) {
        super(DocPrErrorCode.DOCPR_NOT_DRAFT);
        addContext("documentId", documentId);
    }
}
