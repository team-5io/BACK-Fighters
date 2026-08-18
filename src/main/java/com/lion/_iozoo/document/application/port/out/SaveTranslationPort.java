package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.domain.Translation;

public interface SaveTranslationPort {
    Translation save(Translation translation);
}
