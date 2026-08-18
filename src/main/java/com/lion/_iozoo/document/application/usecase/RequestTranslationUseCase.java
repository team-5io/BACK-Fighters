package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.command.RequestTranslationCommand;
import com.lion._iozoo.document.application.result.RequestTranslationResult;

public interface RequestTranslationUseCase {
    RequestTranslationResult translate(Long userId, Long documentId, RequestTranslationCommand command);
}
