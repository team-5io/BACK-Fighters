package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.usecase.CreateDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDocumentService implements CreateDocumentUseCase {

    private final SaveDocumentPort saveDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public Document create(Long userId, CreateDocumentCommand command) {
        log.info("event=document_create_시작 userId={}, teamId={}", userId, command.teamId());

        try {
            teamPermissionChecker.requireMember(command.teamId(), userId);

            Document document = Document.builder()
                    .teamId(command.teamId())
                    .authorId(userId)
                    .title(command.title())
                    .content(command.content())
                    .status(DocumentStatus.DRAFT)
                    .restricted(false)
                    .build();

            Document saved = saveDocumentPort.save(document);

            log.info("event=document_create_완료 userId={}, teamId={}, documentId={}",
                    userId, command.teamId(), saved.getId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=document_create_실패 userId={}, teamId={}, reason={}",
                    userId, command.teamId(), e.getMessage(), e);
            throw e;
        }
    }
}
