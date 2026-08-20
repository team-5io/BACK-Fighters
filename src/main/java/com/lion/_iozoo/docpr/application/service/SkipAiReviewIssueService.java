package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.usecase.SkipAiReviewIssueUseCase;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.AiReviewIssueNotFoundException;
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
public class SkipAiReviewIssueService implements SkipAiReviewIssueUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final CheckDocumentAccessPort checkDocumentAccessPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final LoadAiReviewIssuesPort loadAiReviewIssuesPort;
    private final SaveAiReviewIssuesPort saveAiReviewIssuesPort;

    @Override
    @Transactional
    public AiReviewIssue skip(Long userId, Long docPrId, Long issueId) {
        log.info("event=ai_review_issue_skip_시작 userId={}, docPrId={}, issueId={}", userId, docPrId, issueId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            if (!checkDocumentAccessPort.hasFullAccess(docPr.getDocumentId(), userId)) {
                throw new DocPrAccessDeniedException(docPrId);
            }

            AiReviewIssue issue = loadAiReviewIssuesPort.loadById(issueId)
                    .filter(candidate -> candidate.getDocPrId().equals(docPrId))
                    .orElseThrow(() -> new AiReviewIssueNotFoundException(issueId));

            issue.skip();
            AiReviewIssue updated = saveAiReviewIssuesPort.updateStatus(issueId, AiReviewIssueStatus.SKIPPED);

            log.info("event=ai_review_issue_skip_완료 userId={}, docPrId={}, issueId={}", userId, docPrId, issueId);
            return updated;
        } catch (RuntimeException e) {
            log.warn("event=ai_review_issue_skip_실패 userId={}, docPrId={}, issueId={}, reason={}",
                    userId, docPrId, issueId, e.getMessage(), e);
            throw e;
        }
    }
}
