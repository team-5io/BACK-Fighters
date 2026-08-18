package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class WritingAssistantFailedException extends BusinessException {
    public WritingAssistantFailedException(Long documentId, Throwable cause) {
        super(DocumentErrorCode.WRITING_ASSISTANT_FAILED, DocumentErrorCode.WRITING_ASSISTANT_FAILED.getMessage(), cause);
        addContext("documentId", documentId);
    }
}
