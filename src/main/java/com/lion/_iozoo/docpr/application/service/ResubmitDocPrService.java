package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ResubmitDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.usecase.ResubmitDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotRejectedException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotRequesterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResubmitDocPrService implements ResubmitDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;

    @Override
    @Transactional
    public DocPr resubmit(Long userId, ResubmitDocPrCommand command) {
        log.info("event=docpr_resubmit_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            // 재제출은 요청자(R) 본인만 가능 (기능명세서 "재제출" 권한 기준)
            if (!docPr.getRequesterId().equals(userId)) {
                throw new DocPrNotRequesterException(command.docPrId());
            }

            if (!docPr.isRejected()) {
                throw new DocPrNotRejectedException(command.docPrId());
            }

            DocPrStatus fromStatus = docPr.getStatus();
            docPr.resubmit(command.proposedContent());

            DocPr saved = saveDocPrPort.save(docPr);
            saveDocPrStatusHistoryPort.save(command.docPrId(), fromStatus, DocPrStatus.RESUBMITTED, userId, null);

            log.info("event=docpr_resubmit_완료 userId={}, docPrId={}", userId, command.docPrId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_resubmit_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
