package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.BadRequestException;

public class DocPrSelfApprovalException extends BadRequestException {
    public DocPrSelfApprovalException(Long documentId) {
        super(DocPrErrorCode.DOCPR_SELF_APPROVAL_NOT_ALLOWED);
        addContext("documentId", documentId);
    }
}
