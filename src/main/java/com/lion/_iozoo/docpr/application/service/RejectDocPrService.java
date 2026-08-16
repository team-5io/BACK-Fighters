package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.RejectDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.usecase.RejectDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAlreadyTerminalException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApproverException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RejectDocPrService implements RejectDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;

    @Override
    @Transactional
    public DocPr reject(Long userId, RejectDocPrCommand command) {
        log.info("event=docpr_reject_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            // 반려는 승인권자(A)만 가능 (C 리뷰어 배정 기능 미구현이라 이번 범위에서는 A만)
            if (!docPr.getApproverId().equals(userId)) {
                throw new DocPrNotApproverException(command.docPrId());
            }

            if (docPr.isTerminal()) {
                throw new DocPrAlreadyTerminalException(command.docPrId());
            }

            DocPrStatus fromStatus = docPr.getStatus();
            docPr.reject();

            DocPr saved = saveDocPrPort.save(docPr);
            saveDocPrStatusHistoryPort.save(command.docPrId(), fromStatus, DocPrStatus.REJECTED, userId, command.reason());

            log.info("event=docpr_reject_완료 userId={}, docPrId={}", userId, command.docPrId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_reject_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
