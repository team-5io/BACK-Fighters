package com.lion._iozoo.document.domain.exception;

import com.lion._iozoo.global.exception.NotFoundException;

public class TranslationNotFoundException extends NotFoundException {
    public TranslationNotFoundException(Long translationId) {
        super(DocumentErrorCode.TRANSLATION_NOT_FOUND, DocumentErrorCode.TRANSLATION_NOT_FOUND.getMessage());
        addContext("translationId", translationId);
    }
}
