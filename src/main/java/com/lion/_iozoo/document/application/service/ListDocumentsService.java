package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.usecase.ListDocumentsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListDocumentsService implements ListDocumentsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Page<Document> list(Long userId, Long teamId, Pageable pageable) {
        teamPermissionChecker.requireMember(teamId, userId);

        return loadDocumentPort.loadByTeamId(teamId, userId, pageable);
    }
}
