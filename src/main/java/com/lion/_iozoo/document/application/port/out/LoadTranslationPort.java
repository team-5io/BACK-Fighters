package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Translation;

import java.util.Optional;

public interface LoadTranslationPort {
    Optional<Translation> loadById(Long translationId);
}
