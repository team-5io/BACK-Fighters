package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.application.usecase.GetDocPrHistoryUseCase;
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
public class GetDocPrHistoryService implements GetDocPrHistoryUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final LoadDocPrStatusHistoryPort loadDocPrStatusHistoryPort;
    private final TeamPermissionChecker teamPermissionChecker;

    // 조회 권한은 R/A/C/I 전부(기능명세서 기준) — RACI 세분화가 아직 없으므로
    // 문서가 속한 팀의 팀원이면 누구나 조회 가능하게 구현(상세조회와 동일 기준).
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

            List<DocPrHistoryEntry> history = loadDocPrStatusHistoryPort.loadByDocPrId(docPrId);

            log.info("event=docpr_history_get_완료 userId={}, docPrId={}, count={}", userId, docPrId, history.size());
            return history;
        } catch (RuntimeException e) {
            log.warn("event=docpr_history_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
