package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrReviewsPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrReview;
import com.lion._iozoo.docpr.application.usecase.GetDocPrReviewsUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDocPrReviewsService implements GetDocPrReviewsUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final LoadDocPrReviewsPort loadDocPrReviewsPort;
    private final TeamPermissionChecker teamPermissionChecker;

    // 등록 API와 동일하게, 리뷰어(C) 배정 기능이 없어 대상 문서가 속한 팀의 팀원이면 조회 가능하게 구현.
    @Override
    @Transactional(readOnly = true)
    public List<DocPrReview> getReviews(Long userId, Long docPrId) {
        log.info("event=docpr_reviews_get_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            List<DocPrReview> reviews = loadDocPrReviewsPort.loadByDocPrId(docPrId);

            log.info("event=docpr_reviews_get_완료 userId={}, docPrId={}, count={}", userId, docPrId, reviews.size());
            return reviews;
        } catch (RuntimeException e) {
            log.warn("event=docpr_reviews_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
