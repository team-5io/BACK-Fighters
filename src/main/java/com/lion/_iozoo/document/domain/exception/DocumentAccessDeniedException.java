package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.ForbiddenException;

public class DocumentAccessDeniedException extends ForbiddenException {
    public DocumentAccessDeniedException(Long documentId) {
        super(DocumentErrorCode.DOCUMENT_ACCESS_DENIED, DocumentErrorCode.DOCUMENT_ACCESS_DENIED.getMessage());
        addContext("documentId", documentId);
    }
}
