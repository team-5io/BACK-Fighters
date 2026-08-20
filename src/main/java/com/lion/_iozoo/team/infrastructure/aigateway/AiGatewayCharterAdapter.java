package com.lion._iozoo.team.infrastructure.aigateway;

import com.lion._iozoo.team.application.port.out.RequestAdoptCharterRulesPort;
import com.lion._iozoo.team.application.port.out.RequestCharterDraftPort;
import com.lion._iozoo.team.application.port.out.RequestCharterRulesPort;
import com.lion._iozoo.team.application.port.out.RequestUpdateCharterRulePort;
import com.lion._iozoo.team.application.result.CharterRule;
import com.lion._iozoo.team.domain.exception.CharterGatewayFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Consumer: team (Team Collaboration Charter)
 * Purpose: team-5io/AI-Fighters의 협업 규칙 관련 4개 엔드포인트를 서버 간으로 호출한다.
 * 규칙은 AI-Fighters가 자체 PK(uuid)로 저장·관리하므로 여기서는 순수 프록시만 한다 (우리 DB에 저장 안 함).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayCharterAdapter implements
        RequestCharterDraftPort, RequestCharterRulesPort, RequestUpdateCharterRulePort, RequestAdoptCharterRulesPort {

    private final RestClient aiGatewayRestClient;

    @Override
    public List<CharterRule> requestDraft(Long teamId) {
        try {
            RulesResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/charter/generate")
                    .body(new GenerateRequest(teamId))
                    .retrieve()
                    .body(RulesResponse.class);

            if (response == null) {
                throw new CharterGatewayFailedException("generate teamId=" + teamId, null);
            }
            return toCharterRules(response.rules());
        } catch (CharterGatewayFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_charter_generate_실패 teamId={}, reason={}", teamId, e.getMessage(), e);
            throw new CharterGatewayFailedException("generate teamId=" + teamId, e);
        }
    }

    @Override
    public List<CharterRule> listRules(Long teamId, String status) {
        try {
            RulesResponse response = status == null
                    ? aiGatewayRestClient.get()
                        .uri("/api/ai/charter/rules?teamId={teamId}", teamId)
                        .retrieve().body(RulesResponse.class)
                    : aiGatewayRestClient.get()
                        .uri("/api/ai/charter/rules?teamId={teamId}&status={status}", teamId, status)
                        .retrieve().body(RulesResponse.class);

            if (response == null) {
                throw new CharterGatewayFailedException("list teamId=" + teamId, null);
            }
            return toCharterRules(response.rules());
        } catch (CharterGatewayFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_charter_list_실패 teamId={}, reason={}", teamId, e.getMessage(), e);
            throw new CharterGatewayFailedException("list teamId=" + teamId, e);
        }
    }

    @Override
    public CharterRule updateRule(String ruleId, String title, String content) {
        try {
            RuleOut response = aiGatewayRestClient.patch()
                    .uri("/api/ai/charter/rules/{ruleId}", ruleId)
                    .body(new UpdateRuleRequest(title, content))
                    .retrieve()
                    .body(RuleOut.class);

            if (response == null) {
                throw new CharterGatewayFailedException("update ruleId=" + ruleId, null);
            }
            return toCharterRule(response);
        } catch (CharterGatewayFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_charter_update_실패 ruleId={}, reason={}", ruleId, e.getMessage(), e);
            throw new CharterGatewayFailedException("update ruleId=" + ruleId, e);
        }
    }

    @Override
    public void adopt(Long teamId, List<String> ruleIds, UUID adoptedBy) {
        try {
            aiGatewayRestClient.post()
                    .uri("/api/ai/charter/adopt")
                    .body(new AdoptRequest(teamId, ruleIds, adoptedBy.toString()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_charter_adopt_실패 teamId={}, reason={}", teamId, e.getMessage(), e);
            throw new CharterGatewayFailedException("adopt teamId=" + teamId, e);
        }
    }

    private List<CharterRule> toCharterRules(List<RuleOut> rules) {
        return rules.stream().map(this::toCharterRule).toList();
    }

    private CharterRule toCharterRule(RuleOut rule) {
        return new CharterRule(rule.id(), rule.status(), rule.title(), rule.description());
    }

    private record GenerateRequest(Long teamId) {
    }

    private record UpdateRuleRequest(String title, String description) {
    }

    private record AdoptRequest(Long teamId, List<String> ruleIds, String adoptedBy) {
    }

    private record RuleOut(String id, String status, String title, String description) {
    }

    private record RulesResponse(List<RuleOut> rules) {
    }
}
