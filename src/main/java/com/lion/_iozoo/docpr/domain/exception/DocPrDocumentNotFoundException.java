package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class DocPrDocumentNotFoundException extends NotFoundException {
    public DocPrDocumentNotFoundException(Long documentId) {
        super(DocPrErrorCode.DOCPR_DOCUMENT_NOT_FOUND);
        addContext("documentId", documentId);
    }
}
