package com.lion._iozoo.document.infrastructure.aigateway;

import com.lion._iozoo.document.application.port.out.RequestTranslationPort;
import com.lion._iozoo.document.application.result.TranslationGatewayResult;
import com.lion._iozoo.document.domain.exception.TranslationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Consumer: document (Dev-aware Translation)
 * Purpose: team-5io/AI-Fighters의 POST /api/ai/translations를 서버 간으로 호출한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayTranslationAdapter implements RequestTranslationPort {

    private final RestClient aiGatewayRestClient;

    @Override
    public TranslationGatewayResult requestTranslation(Long documentId, String blockId, String content, String sourceLanguage, String targetLanguage) {
        AiTranslationRequest request = new AiTranslationRequest(
                documentId, blockId, content, sourceLanguage, targetLanguage);

        try {
            AiTranslationResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/translations")
                    .body(request)
                    .retrieve()
                    .body(AiTranslationResponse.class);

            if (response == null) {
                throw new TranslationFailedException(documentId, null);
            }

            return new TranslationGatewayResult(response.translatedContent(), response.preservedTerms());
        } catch (TranslationFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_translation_실패 documentId={}, reason={}", documentId, e.getMessage(), e);
            throw new TranslationFailedException(documentId, e);
        }
    }

    private record AiTranslationRequest(Long documentId, String blockId, String content, String sourceLang, String targetLang) {
    }

    private record AiTranslationResponse(String translatedContent, List<String> preservedTerms, boolean cached) {
    }
}
