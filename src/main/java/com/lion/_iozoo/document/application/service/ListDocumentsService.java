package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.usecase.ListDocumentsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListDocumentsService implements ListDocumentsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Page<Document> list(Long userId, Long teamId, Pageable pageable) {
        log.info("event=document_list_시작 userId={}, teamId={}", userId, teamId);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            Page<Document> result = loadDocumentPort.loadByTeamId(teamId, userId, pageable);

            log.info("event=document_list_완료 userId={}, teamId={}, count={}",
                    userId, teamId, result.getNumberOfElements());
            return result;
        } catch (RuntimeException e) {
            log.warn("event=document_list_실패 userId={}, teamId={}, reason={}",
                    userId, teamId, e.getMessage(), e);
            throw e;
        }
    }
}
