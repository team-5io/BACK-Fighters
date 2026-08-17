package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.BadRequestException;

public class DocumentRaciDuplicateUserException extends BadRequestException {
    public DocumentRaciDuplicateUserException(Long documentId) {
        super(DocumentErrorCode.DOCUMENT_RACI_DUPLICATE_USER);
        addContext("documentId", documentId);
    }
}
