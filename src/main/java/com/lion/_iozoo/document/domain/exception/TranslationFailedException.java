package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.BusinessException;

public class TranslationFailedException extends BusinessException {
    public TranslationFailedException(Long documentId, Throwable cause) {
        super(DocumentErrorCode.TRANSLATION_FAILED, DocumentErrorCode.TRANSLATION_FAILED.getMessage(), cause);
        addContext("documentId", documentId);
    }
}
