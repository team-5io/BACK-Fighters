package com.lion._iozoo.team.infrastructure.aigateway;

import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.result.CharterRuleDraft;
import com.lion._iozoo.team.domain.exception.CharterDraftFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Consumer: team (Team Collaboration Charter)
 * Purpose: team-5io/AI-Fighters의 POST /api/ai/charter/generate를 서버 간으로 호출한다.
 * AI 기능은 초안 생성뿐이고, 생성된 규칙의 저장/조회/수정/삭제/채택은 BE 자체 CRUD다
 * (AI가 주는 규칙 id/status는 우리 쪽에서 안 쓰므로 title/description만 취한다).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayCharterAdapter implements RequestCharterDraftPort {

    private final RestClient aiGatewayRestClient;

    @Override
    public List<CharterRuleDraft> requestDraft(Long teamId) {
        GenerateCharterRequest request = new GenerateCharterRequest(teamId);

        try {
            GenerateCharterResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/charter/generate")
                    .body(request)
                    .retrieve()
                    .body(GenerateCharterResponse.class);

            if (response == null) {
                throw new CharterDraftFailedException(teamId, null);
            }

            return response.rules().stream()
                    .map(rule -> new CharterRuleDraft(rule.title(), rule.description()))
                    .toList();
        } catch (CharterDraftFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_charter_실패 teamId={}, reason={}", teamId, e.getMessage(), e);
            throw new CharterDraftFailedException(teamId, e);
        }
    }

    private record GenerateCharterRequest(Long teamId) {
    }

    private record CharterRuleOut(String id, String status, String title, String description) {
    }

    private record GenerateCharterResponse(List<CharterRuleOut> rules) {
    }
}
