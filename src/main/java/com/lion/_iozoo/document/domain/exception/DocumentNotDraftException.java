package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.BadRequestException;

public class DocumentNotDraftException extends BadRequestException {
    public DocumentNotDraftException(Long documentId) {
        super(DocumentErrorCode.DOCUMENT_NOT_DRAFT);
        addContext("documentId", documentId);
    }
}
