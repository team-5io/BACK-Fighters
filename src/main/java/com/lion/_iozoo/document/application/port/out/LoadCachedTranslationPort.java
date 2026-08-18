package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Translation;

import java.util.Optional;

public interface LoadCachedTranslationPort {
    Optional<Translation> loadByDocumentIdAndTargetLanguage(Long documentId, String targetLanguage);
}
