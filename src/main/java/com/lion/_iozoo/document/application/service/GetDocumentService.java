package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.usecase.GetDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDocumentService implements GetDocumentUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Document getById(Long userId, Long documentId) {
        log.info("event=document_get_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            RaciRole role = RaciRoleLookup.roleOf(loadDocumentRaciPort.loadByDocumentId(documentId), userId);
            if (document.resolveAccessLevel(userId, role) == DocumentAccessLevel.NONE) {
                throw new DocumentAccessDeniedException(documentId);
            }

            log.info("event=document_get_완료 userId={}, documentId={}", userId, documentId);
            return document;
        } catch (RuntimeException e) {
            log.warn("event=document_get_실패 userId={}, documentId={}, reason={}", userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
