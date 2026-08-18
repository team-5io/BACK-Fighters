package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.application.usecase.GetDocPrHistoryUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
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
public class GetDocPrHistoryService implements GetDocPrHistoryUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final LoadDocPrStatusHistoryPort loadDocPrStatusHistoryPort;
    private final CheckDocumentAccessPort checkDocumentAccessPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public List<DocPrHistoryEntry> getHistory(Long userId, Long docPrId) {
        log.info("event=docpr_history_get_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            // Doc PR 이력은 문서 접근수준이 FULL(작성자/R/A/C)인 경우만 조회 가능 — I·역할없음은 차단.
            if (!checkDocumentAccessPort.hasFullAccess(docPr.getDocumentId(), userId)) {
                throw new DocPrAccessDeniedException(docPrId);
            }

            List<DocPrHistoryEntry> history = loadDocPrStatusHistoryPort.loadByDocPrId(docPrId);

            log.info("event=docpr_history_get_완료 userId={}, docPrId={}, count={}", userId, docPrId, history.size());
            return history;
        } catch (RuntimeException e) {
            log.warn("event=docpr_history_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
