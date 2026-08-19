package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ChangeDocPrApproverCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.usecase.ChangeDocPrApproverUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.exception.DocPrAlreadyTerminalException;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrSelfApprovalException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeDocPrApproverService implements ChangeDocPrApproverUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final SaveDocPrPort saveDocPrPort;
    private final TeamPermissionChecker teamPermissionChecker;

    // 원 스펙은 "승인권자 부재 시"가 전제지만, 부재 상태를 추적하는 기능이 없어
    // 팀 관리자가 부재 여부와 무관하게 언제든 승인권자를 교체할 수 있게 구현한다.
    @Override
    @Transactional
    public DocPr changeApprover(Long userId, ChangeDocPrApproverCommand command) {
        log.info("event=docpr_change_approver_시작 userId={}, docPrId={}", userId, command.docPrId());

        try {
            DocPr docPr = loadDocPrPort.loadById(command.docPrId())
                    .orElseThrow(() -> new DocPrNotFoundException(command.docPrId()));

            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(docPr.getDocumentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(docPr.getDocumentId()));

            // 대체 승인권자 지정은 팀 관리자만 가능 (기능명세서 권한 기준)
            teamPermissionChecker.requireAdmin(document.teamId(), userId);

            if (docPr.isTerminal()) {
                throw new DocPrAlreadyTerminalException(command.docPrId());
            }

            Long newApproverId = teamPermissionChecker.resolveUserId(document.teamId(), command.newApproverMemberId());

            if (newApproverId.equals(docPr.getRequesterId())) {
                throw new DocPrSelfApprovalException(command.docPrId());
            }

            docPr.changeApprover(newApproverId);
            DocPr saved = saveDocPrPort.save(docPr);

            log.info("event=docpr_change_approver_완료 userId={}, docPrId={}, newApproverId={}",
                    userId, command.docPrId(), newApproverId);
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_change_approver_실패 userId={}, docPrId={}, reason={}",
                    userId, command.docPrId(), e.getMessage(), e);
            throw e;
        }
    }
}
