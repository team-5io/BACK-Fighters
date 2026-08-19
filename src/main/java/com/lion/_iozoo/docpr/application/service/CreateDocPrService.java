package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.usecase.CreateDocPrUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotDraftException;
import com.lion._iozoo.docpr.domain.exception.DocPrRequesterNotAuthorException;
import com.lion._iozoo.docpr.domain.exception.DocPrSelfApprovalException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDocPrService implements CreateDocPrUseCase {

    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final SaveDocPrPort saveDocPrPort;

    @Override
    @Transactional
    public DocPr create(Long userId, CreateDocPrCommand command) {
        log.info("event=docpr_create_시작 documentId={}, userId={}", command.documentId(), userId);

        try {
            DocumentSummary document = loadDocumentForDocPrPort.loadSummary(command.documentId())
                    .orElseThrow(() -> new DocPrDocumentNotFoundException(command.documentId()));

            // 초안 → Doc PR 전환은 문서 작성자(R)만 가능 (기능명세서 "초안 → Doc PR 전환" 권한 기준)
            if (!document.authorId().equals(userId)) {
                throw new DocPrRequesterNotAuthorException(command.documentId());
            }

            if (!document.draft()) {
                throw new DocPrNotDraftException(command.documentId());
            }

            Long approverId = teamPermissionChecker.resolveUserId(document.teamId(), command.approverMemberId());

            if (approverId.equals(userId)) {
                throw new DocPrSelfApprovalException(command.documentId());
            }

            DocPr docPr = DocPr.builder()
                    .documentId(command.documentId())
                    .requesterId(userId)
                    .approverId(approverId)
                    .proposedContent(command.proposedContent())
                    .status(DocPrStatus.CREATED)
                    .build();

            DocPr saved = saveDocPrPort.save(docPr);

            log.info("event=docpr_create_완료 documentId={}, userId={}, docPrId={}",
                    command.documentId(), userId, saved.getId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=docpr_create_실패 documentId={}, userId={}, reason={}",
                    command.documentId(), userId, e.getMessage(), e);
            throw e;
        }
    }
}
