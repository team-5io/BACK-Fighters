package com.lion._iozoo.document.application.port.out;

import com.lion._iozoo.document.application.result.TranslationGatewayResult;

public interface RequestTranslationPort {
    // 실패(연결 실패/타임아웃/비2xx) 시 TranslationFailedException을 던진다.
    TranslationGatewayResult requestTranslation(Long documentId, String blockId, String content, String sourceLanguage, String targetLanguage);
}
