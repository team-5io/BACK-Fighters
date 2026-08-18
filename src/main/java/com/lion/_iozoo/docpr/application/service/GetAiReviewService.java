package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.usecase.GetAiReviewUseCase;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.AiReviewNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAiReviewService implements GetAiReviewUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final CheckDocumentAccessPort checkDocumentAccessPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadAiReviewPort loadAiReviewPort;

    @Override
    @Transactional(readOnly = true)
    public AiReview getByDocPrId(Long userId, Long docPrId) {
        log.info("event=ai_review_get_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            if (!checkDocumentAccessPort.hasFullAccess(docPr.getDocumentId(), userId)) {
                throw new DocPrAccessDeniedException(docPrId);
            }

            AiReview review = loadAiReviewPort.loadByDocPrId(docPrId)
                    .orElseThrow(() -> new AiReviewNotFoundException(docPrId));

            log.info("event=ai_review_get_완료 userId={}, docPrId={}", userId, docPrId);
            return review;
        } catch (RuntimeException e) {
            log.warn("event=ai_review_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
