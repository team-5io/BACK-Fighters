package com.lion._iozoo.document.infrastructure.aigateway;

import com.lion._iozoo.document.application.port.out.RequestWritingSuggestionsPort;
import com.lion._iozoo.document.application.result.WritingSuggestionResult;
import com.lion._iozoo.document.domain.exception.WritingAssistantFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Consumer: document (AI Writing Assistant)
 * Purpose: team-5io/AI-Fighters의 POST /api/ai/writing-assistant/suggestions를 서버 간으로 호출한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayWritingAssistantAdapter implements RequestWritingSuggestionsPort {

    private final RestClient aiGatewayRestClient;

    @Override
    public List<WritingSuggestionResult> requestSuggestions(Long documentId, String content, String cursorContext) {
        SuggestionRequest request = new SuggestionRequest(documentId, content, cursorContext);

        try {
            SuggestionResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/writing-assistant/suggestions")
                    .body(request)
                    .retrieve()
                    .body(SuggestionResponse.class);

            if (response == null) {
                throw new WritingAssistantFailedException(documentId, null);
            }

            return response.suggestions().stream()
                    .map(s -> new WritingSuggestionResult(s.type(), s.text()))
                    .toList();
        } catch (WritingAssistantFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_writing_assistant_실패 documentId={}, reason={}", documentId, e.getMessage(), e);
            throw new WritingAssistantFailedException(documentId, e);
        }
    }

    private record SuggestionRequest(Long documentId, String content, String cursorContext) {
    }

    private record Suggestion(String type, String text) {
    }

    private record SuggestionResponse(List<Suggestion> suggestions) {
    }
}
