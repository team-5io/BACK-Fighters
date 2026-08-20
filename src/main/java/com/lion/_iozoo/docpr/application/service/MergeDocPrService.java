package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.MergeDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
import com.lion._iozoo.docpr.application.port.out.RecordDocumentVersionPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.usecase.MergeDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApprovedException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApproverException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeDocPrService implements MergeDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;
    private final MarkDocumentOfficialPort markDocumentOfficialPort;
    private final RecordDocumentVersionPort recordDocumentVersionPort;

    // 원 스펙의 Merge 차단 규칙(DocumentLion 반려·사람리뷰 미완료·충돌 미해결)은 해당 기능 미구현으로
    // 이번 범위에서는 검사하지 않는다. APPROVED 상태 여부만 확인한다 (merge-check와 동일 기준).
    @Override
    @Transactional
    public DocPr merge(Long userId, MergeDocPrCommand command) {
        log.info("event=docpr_merge_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            if (!docPr.getApproverId().equals(userId)) {
                throw new DocPrNotApproverException(command.docPrId());
            }

            if (!docPr.isApproved()) {
                throw new DocPrNotApprovedException(command.docPrId());
            }

            DocPrStatus fromStatus = docPr.getStatus();
            docPr.merge(LocalDateTime.now());

            DocPr saved = saveDocPrPort.save(docPr);
            markDocumentOfficialPort.markOfficial(docPr.getDocumentId());
            recordDocumentVersionPort.record(docPr.getDocumentId(), docPr.getId());
            saveDocPrStatusHistoryPort.save(command.docPrId(), fromStatus, DocPrStatus.MERGED, userId, null);

            log.info("event=docpr_merge_완료 userId={}, docPrId={}, documentId={}",
                    userId, command.docPrId(), docPr.getDocumentId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_merge_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
