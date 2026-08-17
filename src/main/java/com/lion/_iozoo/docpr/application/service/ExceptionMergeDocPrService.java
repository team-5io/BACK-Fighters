package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ExceptionMergeDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
import com.lion._iozoo.docpr.application.port.out.RecordDocumentVersionPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.usecase.ExceptionMergeDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAlreadyTerminalException;
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
public class ExceptionMergeDocPrService implements ExceptionMergeDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;
    private final MarkDocumentOfficialPort markDocumentOfficialPort;
    private final RecordDocumentVersionPort recordDocumentVersionPort;

    // 일반 Merge 확정과 달리 APPROVED 상태가 아니어도 병합할 수 있다 (차단 조건을 예외적으로 건너뜀).
    // 단, 이미 종료된(APPROVED/REJECTED/MERGED) Doc PR은 예외 Merge도 불가능하다.
    @Override
    @Transactional
    public DocPr mergeWithException(Long userId, ExceptionMergeDocPrCommand command) {
        log.info("event=docpr_exception_merge_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            if (!docPr.getApproverId().equals(userId)) {
                throw new DocPrNotApproverException(command.docPrId());
            }

            if (docPr.isTerminal()) {
                throw new DocPrAlreadyTerminalException(command.docPrId());
            }

            DocPrStatus fromStatus = docPr.getStatus();
            docPr.mergeWithException(LocalDateTime.now(), command.reason());

            DocPr saved = saveDocPrPort.save(docPr);
            markDocumentOfficialPort.markOfficial(docPr.getDocumentId(), docPr.getProposedContent());
            recordDocumentVersionPort.record(docPr.getDocumentId(), docPr.getProposedContent(), docPr.getId());
            saveDocPrStatusHistoryPort.save(command.docPrId(), fromStatus, DocPrStatus.MERGED, userId,
                    "[예외 Merge] " + command.reason());

            log.info("event=docpr_exception_merge_완료 userId={}, docPrId={}, documentId={}",
                    userId, command.docPrId(), docPr.getDocumentId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_exception_merge_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
