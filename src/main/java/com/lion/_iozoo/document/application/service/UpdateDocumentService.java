package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.usecase.UpdateDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotDraftException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateDocumentService implements UpdateDocumentUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public Document update(Long userId, Long documentId, UpdateDocumentCommand command) {
        log.info("event=document_update_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            // 문서 편집은 작성자(R)만 가능 (Doc PR API 명세서 "문서 편집" 사용 계층 기준)
            if (!document.getAuthorId().equals(userId)) {
                throw new DocumentAccessDeniedException(documentId);
            }

            if (!document.isDraft()) {
                throw new DocumentNotDraftException(documentId);
            }

            document.update(command.title(), command.content());

            Document saved = saveDocumentPort.save(document);

            log.info("event=document_update_완료 userId={}, documentId={}", userId, documentId);
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=document_update_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
