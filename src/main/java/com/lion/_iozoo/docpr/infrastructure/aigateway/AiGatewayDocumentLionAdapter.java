package com.lion._iozoo.docpr.infrastructure.aigateway;

import com.lion._iozoo.docpr.application.port.out.RequestDocumentLionReviewPort;
import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;
import com.lion._iozoo.docpr.domain.exception.AiReviewFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Consumer: docpr (DocumentLion)
 * Purpose: team-5io/AI-Fighters의 POST /api/ai/document-lion/reviews를 서버 간으로 호출한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayDocumentLionAdapter implements RequestDocumentLionReviewPort {

    private static final String TRIGGER_TYPE_MANUAL = "manual";
    private static final String ISSUE_TYPE_CONFLICT = "conflict";
    private static final String ISSUE_TYPE_INCONSISTENCY = "inconsistency";
    private static final String ISSUE_TYPE_CHARTER_VIOLATION = "charter_violation";

    private final RestClient aiGatewayRestClient;

    @Override
    public DocumentLionGatewayResult requestReview(Long documentId, Long docPrId, Long teamId, UUID requestedBy, String content) {
        AiReviewRequest request = new AiReviewRequest(
                String.valueOf(documentId), String.valueOf(docPrId), String.valueOf(teamId),
                TRIGGER_TYPE_MANUAL, requestedBy.toString(), content);

        try {
            AiReviewResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/document-lion/reviews")
                    .body(request)
                    .retrieve()
                    .body(AiReviewResponse.class);

            if (response == null) {
                throw new AiReviewFailedException(docPrId, null);
            }

            return toGatewayResult(response.issues());
        } catch (AiReviewFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_document_lion_실패 docPrId={}, reason={}", docPrId, e.getMessage(), e);
            throw new AiReviewFailedException(docPrId, e);
        }
    }

    private DocumentLionGatewayResult toGatewayResult(List<AiReviewIssue> issues) {
        boolean hasConflict = issues.stream().anyMatch(issue -> ISSUE_TYPE_CONFLICT.equals(issue.issueType()));
        boolean hasInconsistency = issues.stream().anyMatch(issue -> ISSUE_TYPE_INCONSISTENCY.equals(issue.issueType()));
        boolean violatesCharter = issues.stream().anyMatch(issue -> ISSUE_TYPE_CHARTER_VIOLATION.equals(issue.issueType()));
        String evidence = issues.isEmpty() ? null : issues.stream()
                .map(issue -> "[%s/%s] %s".formatted(issue.severity(), issue.issueType(), issue.description()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse(null);

        return new DocumentLionGatewayResult(hasConflict, !hasInconsistency, violatesCharter, evidence);
    }

    private record AiReviewRequest(
            String documentId, String docPrId, String teamId, String triggerType, String requestedBy, String content) {
    }

    private record AiReviewIssue(
            String severity, String issueType, String description, Long relatedDocumentId,
            String charterRuleId, String locationRef) {
    }

    private record AiReviewResponse(String reviewId, String overallVerdict, List<AiReviewIssue> issues) {
    }
}
