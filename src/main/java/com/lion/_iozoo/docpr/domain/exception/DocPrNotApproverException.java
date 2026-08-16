package com.lion._iozoo.docpr.domain.exception;

import com.lion._iozoo.global.exception.ForbiddenException;

public class DocPrNotApproverException extends ForbiddenException {
    public DocPrNotApproverException(Long docPrId) {
        super(DocPrErrorCode.DOCPR_NOT_APPROVER, DocPrErrorCode.DOCPR_NOT_APPROVER.getMessage());
        addContext("docPrId", docPrId);
    }
}
