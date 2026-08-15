package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.usecase.SearchDocumentsUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchDocumentsService implements SearchDocumentsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Page<Document> search(Long userId, Long teamId, String keyword, Pageable pageable) {
        teamPermissionChecker.requireMember(teamId, userId);

        return loadDocumentPort.searchByKeyword(teamId, keyword, pageable);
    }
}
