package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ApproveDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.usecase.ApproveDocPrUseCase;
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
public class ApproveDocPrService implements ApproveDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;

    @Override
    @Transactional
    public DocPr approve(Long userId, ApproveDocPrCommand command) {
        log.info("event=docpr_approve_시작 userId={}, docPrId={}", userId, command.docPrId());

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
            docPr.approve();

            DocPr saved = saveDocPrPort.save(docPr);
            saveDocPrStatusHistoryPort.save(command.docPrId(), fromStatus, DocPrStatus.APPROVED, userId, null);

            log.info("event=docpr_approve_완료 userId={}, docPrId={}", userId, command.docPrId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_approve_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
