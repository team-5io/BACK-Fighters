package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;
import com.lion._iozoo.document.application.usecase.GetMyDocumentPermissionUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMyDocumentPermissionService implements GetMyDocumentPermissionUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadDocumentRaciPort loadDocumentRaciPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public MyDocumentPermissionResult getMyPermission(Long userId, Long documentId) {
        log.info("event=document_my_permission_조회_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            RaciRole role = RaciRoleLookup.roleOf(loadDocumentRaciPort.loadByDocumentId(documentId), userId);
            DocumentAccessLevel accessLevel = document.resolveAccessLevel(userId, role);
            boolean isAuthor = document.getAuthorId().equals(userId);

            MyDocumentPermissionResult result = new MyDocumentPermissionResult(
                    documentId, role, accessLevel, isAuthor, accessLevel == DocumentAccessLevel.FULL);

            log.info("event=document_my_permission_조회_완료 userId={}, documentId={}, accessLevel={}",
                    userId, documentId, accessLevel);
            return result;
        } catch (RuntimeException e) {
            log.warn("event=document_my_permission_조회_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
