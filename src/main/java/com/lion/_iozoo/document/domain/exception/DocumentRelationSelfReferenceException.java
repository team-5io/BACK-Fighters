package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.BadRequestException;

public class DocumentRelationSelfReferenceException extends BadRequestException {
    public DocumentRelationSelfReferenceException(Long documentId) {
        super(DocumentErrorCode.DOCUMENT_RELATION_SELF_REFERENCE);
        addContext("documentId", documentId);
    }
}
