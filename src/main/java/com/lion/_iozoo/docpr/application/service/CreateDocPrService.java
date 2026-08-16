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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDocPrService implements CreateDocPrUseCase {

    private final LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final SaveDocPrPort saveDocPrPort;

    @Override
    @Transactional
    public DocPr create(Long userId, CreateDocPrCommand command) {
        DocumentSummary document = loadDocumentForDocPrPort.loadSummary(command.documentId())
                .orElseThrow(() -> new DocPrDocumentNotFoundException(command.documentId()));

        // 초안 → Doc PR 전환은 문서 작성자(R)만 가능 (기능명세서 "초안 → Doc PR 전환" 권한 기준)
        if (!document.authorId().equals(userId)) {
            throw new DocPrRequesterNotAuthorException(command.documentId());
        }

        if (!document.draft()) {
            throw new DocPrNotDraftException(command.documentId());
        }

        if (command.approverId().equals(userId)) {
            throw new DocPrSelfApprovalException(command.documentId());
        }

        teamPermissionChecker.requireMember(document.teamId(), command.approverId());

        DocPr docPr = DocPr.builder()
                .documentId(command.documentId())
                .requesterId(userId)
                .approverId(command.approverId())
                .proposedContent(command.proposedContent())
                .status(DocPrStatus.CREATED)
                .build();

        return saveDocPrPort.save(docPr);
    }
}
