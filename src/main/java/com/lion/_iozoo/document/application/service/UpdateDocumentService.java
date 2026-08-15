package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.usecase.UpdateDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotDraftException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDocumentService implements UpdateDocumentUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public Document update(Long userId, Long documentId, UpdateDocumentCommand command) {
        Document document = loadDocumentPort.loadById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        teamPermissionChecker.requireMember(document.getTeamId(), userId);

        if (!document.isDraft()) {
            throw new DocumentNotDraftException(documentId);
        }

        document.update(command.title(), command.content());

        return saveDocumentPort.save(document);
    }
}
