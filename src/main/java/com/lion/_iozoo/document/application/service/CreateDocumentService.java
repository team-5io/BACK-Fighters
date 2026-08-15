package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.usecase.CreateDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDocumentService implements CreateDocumentUseCase {

    private final SaveDocumentPort saveDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public Document create(Long userId, CreateDocumentCommand command) {
        teamPermissionChecker.requireMember(command.teamId(), userId);

        Document document = Document.builder()
                .teamId(command.teamId())
                .authorId(userId)
                .title(command.title())
                .content(command.content())
                .status(DocumentStatus.DRAFT)
                .restricted(false)
                .build();

        return saveDocumentPort.save(document);
    }
}
