package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.application.result.NextAssigneeInfoResult;
import com.lion._iozoo.docpr.application.usecase.GetNextAssigneeInfoUseCase;
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
public class GetNextAssigneeInfoService implements GetNextAssigneeInfoUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final LoadDocPrStatusHistoryPort loadDocPrStatusHistoryPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public NextAssigneeInfoResult getInfo(Long userId, Long docPrId) {
        log.info("event=docpr_next_assignee_get_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            // 인수인계 정보 위치 = 가장 최근 상태 전이 이력(누가, 언제, 왜 이 상태가 됐는지).
            List<DocPrHistoryEntry> history = loadDocPrStatusHistoryPort.loadByDocPrId(docPrId);
            DocPrHistoryEntry latestHandoff = history.isEmpty() ? null : history.get(history.size() - 1);

            NextAssigneeInfoResult result = new NextAssigneeInfoResult(
                    docPrId, docPr.getStatus(), docPr.needsNextAssignee(), docPr.getNextAssigneeId(), latestHandoff);

            log.info("event=docpr_next_assignee_get_완료 userId={}, docPrId={}, needsNextAssignee={}",
                    userId, docPrId, result.needsNextAssignee());
            return result;
        } catch (RuntimeException e) {
            log.warn("event=docpr_next_assignee_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
