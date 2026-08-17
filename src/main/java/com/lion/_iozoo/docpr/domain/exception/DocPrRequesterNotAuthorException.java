package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ForbiddenException;

public class DocPrRequesterNotAuthorException extends ForbiddenException {
    public DocPrRequesterNotAuthorException(Long documentId) {
        super(DocPrErrorCode.DOCPR_REQUESTER_NOT_AUTHOR, DocPrErrorCode.DOCPR_REQUESTER_NOT_AUTHOR.getMessage());
        addContext("documentId", documentId);
    }
}
