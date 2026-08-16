package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.AddDocPrReviewCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrReviewPort;
import com.lion._iozoo.docpr.application.result.DocPrReview;
import com.lion._iozoo.docpr.application.usecase.AddDocPrReviewUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
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
public class AddDocPrReviewService implements AddDocPrReviewUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final SaveDocPrReviewPort saveDocPrReviewPort;
    private final TeamPermissionChecker teamPermissionChecker;

    // 사용 계층은 C(리뷰어)/A이지만, 리뷰어(C) 배정 기능이 아직 없어
    // 대상 문서가 속한 팀의 팀원이면 누구나 리뷰 의견을 등록할 수 있게 구현한다.
    @Override
    @Transactional
    public DocPrReview addReview(Long userId, AddDocPrReviewCommand command) {
        log.info("event=docpr_review_add_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            DocPrReview review = saveDocPrReviewPort.save(command.docPrId(), userId, command.comment());

            log.info("event=docpr_review_add_완료 userId={}, docPrId={}, reviewId={}",
                    userId, command.docPrId(), review.id());
            return review;
        } catch (RuntimeException e) {
            log.warn("event=docpr_review_add_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
