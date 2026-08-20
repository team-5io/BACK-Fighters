package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentLionReviewRequest;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentBlocksForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadRelatedDocumentsForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.RequestDocumentLionReviewPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewPort;
import com.lion._iozoo.docpr.application.result.AiReviewIssueResult;
import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;
import com.lion._iozoo.docpr.application.usecase.RequestAiReviewUseCase;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAiReviewService implements RequestAiReviewUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final CheckDocumentAccessPort checkDocumentAccessPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadUserPort loadUserPort;
    private final LoadDocumentBlocksForDocPrPort loadDocumentBlocksForDocPrPort;
    private final LoadRelatedDocumentsForDocPrPort loadRelatedDocumentsForDocPrPort;
    private final RequestDocumentLionReviewPort requestDocumentLionReviewPort;
    private final SaveAiReviewPort saveAiReviewPort;
    private final SaveAiReviewIssuesPort saveAiReviewIssuesPort;

    @Override
    @Transactional
    public AiReview request(Long userId, Long docPrId) {
        log.info("event=ai_review_request_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            if (!checkDocumentAccessPort.hasFullAccess(docPr.getDocumentId(), userId)) {
                throw new DocPrAccessDeniedException(docPrId);
            }

            User requester = loadUserPort.loadUserById(userId).orElseThrow(UserNotFoundException::new);

            DocumentLionReviewRequest reviewRequest = new DocumentLionReviewRequest(
                    docPr.getDocumentId(), docPrId, document.teamId(), requester.getPublicId(), docPr.getProposedContent(),
                    loadDocumentBlocksForDocPrPort.loadFlattenedBlocks(docPr.getDocumentId()),
                    loadRelatedDocumentsForDocPrPort.loadVisibleRelatedDocuments(docPr.getDocumentId(), userId));

            DocumentLionGatewayResult gatewayResult = requestDocumentLionReviewPort.requestReview(reviewRequest);

            AiReview saved = saveAiReviewPort.saveOrReplace(AiReview.builder()
                    .docPrId(docPrId)
                    .hasConflict(gatewayResult.hasConflict())
                    .isConsistent(gatewayResult.isConsistent())
                    .violatesCharter(gatewayResult.violatesCharter())
                    .evidence(gatewayResult.evidence())
                    .reviewedAt(LocalDateTime.now())
                    .build());

            saveAiReviewIssuesPort.saveNewIssues(docPrId, toDetectedIssues(docPrId, gatewayResult.issues()));

            log.info("event=ai_review_request_완료 userId={}, docPrId={}", userId, docPrId);
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=ai_review_request_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }

    private List<AiReviewIssue> toDetectedIssues(Long docPrId, List<AiReviewIssueResult> issues) {
        LocalDateTime now = LocalDateTime.now();
        return issues.stream()
                .map(issue -> AiReviewIssue.builder()
                        .docPrId(docPrId)
                        .severity(issue.severity())
                        .issueType(issue.issueType())
                        .description(issue.description())
                        .relatedDocumentId(issue.relatedDocumentId())
                        .charterRuleId(issue.charterRuleId())
                        .blockId(issue.blockId())
                        .quote(issue.quote())
                        .status(AiReviewIssueStatus.UNRESOLVED)
                        .createdAt(now)
                        .build())
                .toList();
    }
}
