package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.domain.Translation;

public interface GetTranslationUseCase {
    Translation getById(Long userId, Long documentId, Long translationId);
}
