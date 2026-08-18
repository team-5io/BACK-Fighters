package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.RequestDocumentLionReviewPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewPort;
import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;
import com.lion._iozoo.docpr.application.usecase.RequestAiReviewUseCase;
import com.lion._iozoo.docpr.domain.AiReview;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAiReviewService implements RequestAiReviewUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final CheckDocumentAccessPort checkDocumentAccessPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadUserPort loadUserPort;
    private final RequestDocumentLionReviewPort requestDocumentLionReviewPort;
    private final SaveAiReviewPort saveAiReviewPort;

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

            DocumentLionGatewayResult gatewayResult = requestDocumentLionReviewPort.requestReview(
                    docPr.getDocumentId(), docPrId, document.teamId(), requester.getPublicId(), docPr.getProposedContent());

            AiReview saved = saveAiReviewPort.saveOrReplace(AiReview.builder()
                    .docPrId(docPrId)
                    .hasConflict(gatewayResult.hasConflict())
                    .isConsistent(gatewayResult.isConsistent())
                    .violatesCharter(gatewayResult.violatesCharter())
                    .evidence(gatewayResult.evidence())
                    .reviewedAt(LocalDateTime.now())
                    .build());

            log.info("event=ai_review_request_완료 userId={}, docPrId={}", userId, docPrId);
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=ai_review_request_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
