package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.result.MergeCheckResult;
import com.lion._iozoo.docpr.application.usecase.MergeCheckDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApproverException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeCheckDocPrService implements MergeCheckDocPrUseCase {

    private final LoadDocPrPort loadDocPrPort;

    // 원 스펙은 DocumentLion 반려·사람 리뷰 미완료·충돌 미해결까지 확인해야 하지만,
    // 해당 기능들이 아직 구현되지 않아 이번 범위는 APPROVED 상태 여부만 확인한다.
    @Override
    @Transactional(readOnly = true)
    public MergeCheckResult checkMergeable(Long userId, Long docPrId) {
        log.info("event=docpr_merge_check_시작 userId={}, docPrId={}", userId, docPrId);

        try {
            DocPr docPr = loadDocPrPort.loadById(docPrId)
                    .orElseThrow(() -> new DocPrNotFoundException(docPrId));

            if (!docPr.getApproverId().equals(userId)) {
                throw new DocPrNotApproverException(docPrId);
            }

            MergeCheckResult result = docPr.getStatus() == DocPrStatus.APPROVED
                    ? new MergeCheckResult(true, null)
                    : new MergeCheckResult(false, "승인된 Doc PR만 병합할 수 있습니다.");

            log.info("event=docpr_merge_check_완료 userId={}, docPrId={}, mergeable={}",
                    userId, docPrId, result.mergeable());
            return result;
        } catch (RuntimeException e) {
            log.warn("event=docpr_merge_check_실패 userId={}, docPrId={}, reason={}",
                    userId, docPrId, e.getMessage(), e);
            throw e;
        }
    }
}
