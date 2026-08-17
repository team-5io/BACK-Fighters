package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.application.usecase.GetDocumentVersionsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDocumentVersionsService implements GetDocumentVersionsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentVersionsPort loadDocumentVersionsPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentVersion> getVersions(Long userId, Long documentId) {
        log.info("event=document_versions_조회_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            var role = RaciRoleLookup.roleOf(loadDocumentRaciPort.loadByDocumentId(documentId), userId);
            if (document.resolveAccessLevel(userId, role) == DocumentAccessLevel.NONE) {
                throw new DocumentAccessDeniedException(documentId);
            }

            List<DocumentVersion> versions = loadDocumentVersionsPort.loadByDocumentId(documentId);

            log.info("event=document_versions_조회_완료 userId={}, documentId={}, count={}",
                    userId, documentId, versions.size());
            return versions;
        } catch (RuntimeException e) {
            log.warn("event=document_versions_조회_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
