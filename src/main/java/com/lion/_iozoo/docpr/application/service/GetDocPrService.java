package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.usecase.GetDocPrUseCase;
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
public class GetDocPrService implements GetDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public DocPr getById(Long userId, Long docPrId) {
        log.info("event=docpr_get_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            // 조회 권한은 R/A/C/I 전부(기능명세서 "Doc PR 상세/상태 조회" 사용 계층 기준) —
            // RACI 세분화가 아직 없으므로 문서가 속한 팀의 팀원이면 누구나 조회 가능하게 구현.
            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            teamPermissionChecker.requireMember(document.teamId(), userId);

            log.info("event=docpr_get_완료 userId={}, docPrId={}", userId, docPrId);
            return docPr;
        } catch (RuntimeException e) {
            log.warn("event=docpr_get_실패 userId={}, docPrId={}, reason={}", userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
