package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class DocumentNotFoundException extends NotFoundException {
    public DocumentNotFoundException(Long documentId) {
        super(DocumentErrorCode.DOCUMENT_NOT_FOUND);
        addContext("documentId", documentId);
    }
}
