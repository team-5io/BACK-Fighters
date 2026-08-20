package com.lion._iozoo.docpr.infrastructure.aigateway;

import com.lion._iozoo.docpr.application.port.out.DocumentBlockContent;
import com.lion._iozoo.docpr.application.port.out.DocumentLionReviewRequest;
import com.lion._iozoo.docpr.application.port.out.RelatedDocumentContent;
import com.lion._iozoo.docpr.application.port.out.RequestDocumentLionReviewPort;
import com.lion._iozoo.docpr.application.result.AiReviewIssueResult;
import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;
import com.lion._iozoo.docpr.domain.exception.AiReviewFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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
    public DocumentLionGatewayResult requestReview(DocumentLionReviewRequest request) {
        AiReviewRequest body = new AiReviewRequest(
                request.documentId(), request.docPrId(), request.teamId(),
                TRIGGER_TYPE_MANUAL, request.requestedBy().toString(), request.content(),
                toBlockRequests(request.blocks()), toRelatedDocumentRequests(request.relatedDocuments()));

        try {
            AiReviewResponse response = aiGatewayRestClient.post()
                    .uri("/api/ai/document-lion/reviews")
                    .body(body)
                    .retrieve()
                    .body(AiReviewResponse.class);

            if (response == null) {
                throw new AiReviewFailedException(request.docPrId(), null);
            }

            return toGatewayResult(response.issues());
        } catch (AiReviewFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.warn("event=ai_gateway_document_lion_실패 docPrId={}, reason={}", request.docPrId(), e.getMessage(), e);
            throw new AiReviewFailedException(request.docPrId(), e);
        }
    }

    private List<BlockRequest> toBlockRequests(List<DocumentBlockContent> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        return blocks.stream().map(block -> new BlockRequest(block.blockId(), block.content())).toList();
    }

    private List<RelatedDocumentRequest> toRelatedDocumentRequests(List<RelatedDocumentContent> relatedDocuments) {
        if (relatedDocuments == null || relatedDocuments.isEmpty()) {
            return null;
        }
        return relatedDocuments.stream()
                .map(doc -> new RelatedDocumentRequest(
                        doc.documentId(), doc.title(), doc.content(), doc.relationType(), doc.direction()))
                .toList();
    }

    private DocumentLionGatewayResult toGatewayResult(List<AiReviewIssue> issues) {
        boolean hasConflict = issues.stream().anyMatch(issue -> ISSUE_TYPE_CONFLICT.equals(issue.issueType()));
        boolean hasInconsistency = issues.stream().anyMatch(issue -> ISSUE_TYPE_INCONSISTENCY.equals(issue.issueType()));
        boolean violatesCharter = issues.stream().anyMatch(issue -> ISSUE_TYPE_CHARTER_VIOLATION.equals(issue.issueType()));
        String evidence = issues.isEmpty() ? null : issues.stream()
                .map(issue -> "[%s/%s] %s".formatted(issue.severity(), issue.issueType(), issue.description()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse(null);

        List<AiReviewIssueResult> issueResults = issues.stream()
                .map(issue -> new AiReviewIssueResult(
                        issue.severity(), issue.issueType(), issue.description(), issue.relatedDocumentId(),
                        issue.charterRuleId(),
                        issue.locationRef() == null ? null : issue.locationRef().blockId(),
                        issue.locationRef() == null ? null : issue.locationRef().quote()))
                .toList();

        return new DocumentLionGatewayResult(hasConflict, !hasInconsistency, violatesCharter, evidence, issueResults);
    }

    private record BlockRequest(String blockId, String content) {
    }

    private record RelatedDocumentRequest(
            Long documentId, String title, String content, String relationType, String direction) {
    }

    private record AiReviewRequest(
            Long documentId, Long docPrId, Long teamId, String triggerType, String requestedBy, String content,
            List<BlockRequest> blocks, List<RelatedDocumentRequest> relatedDocuments) {
    }

    private record LocationRef(String blockId, String quote) {
    }

    private record AiReviewIssue(
            String severity, String issueType, String description, Long relatedDocumentId,
            String charterRuleId, LocationRef locationRef) {
    }

    private record AiReviewResponse(String reviewId, String overallVerdict, List<AiReviewIssue> issues) {
    }
}
