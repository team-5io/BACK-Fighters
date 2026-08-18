package com.lion._iozoo.document.application.result;

import com.lion._iozoo.document.domain.Translation;

public record RequestTranslationResult(Translation translation, boolean cached) {
}
