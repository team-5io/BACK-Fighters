package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.CreateDocumentRelationCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentRelationPort;
import com.lion._iozoo.document.application.usecase.CreateDocumentRelationUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.DocumentRelationSelfReferenceException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDocumentRelationService implements CreateDocumentRelationUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentRelationPort saveDocumentRelationPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public DocumentRelation create(Long userId, Long sourceDocumentId, CreateDocumentRelationCommand command) {
        log.info("event=document_relation_create_시작 userId={}, sourceDocumentId={}, targetDocumentId={}",
                userId, sourceDocumentId, command.targetDocumentId());

        try {
            if (sourceDocumentId.equals(command.targetDocumentId())) {
                throw new DocumentRelationSelfReferenceException(sourceDocumentId);
            }

            Document source = loadDocumentPort.loadById(sourceDocumentId)
                    .orElseThrow(() -> new DocumentNotFoundException(sourceDocumentId));

            teamPermissionChecker.requireMember(source.getTeamId(), userId);

            loadDocumentPort.loadById(command.targetDocumentId())
                    .orElseThrow(() -> new DocumentNotFoundException(command.targetDocumentId()));

            DocumentRelation relation = DocumentRelation.builder()
                    .sourceDocumentId(sourceDocumentId)
                    .targetDocumentId(command.targetDocumentId())
                    .relationType(command.relationType())
                    .createdAt(LocalDateTime.now())
                    .build();

            DocumentRelation saved = saveDocumentRelationPort.save(relation);

            log.info("event=document_relation_create_완료 userId={}, sourceDocumentId={}, targetDocumentId={}, relationId={}",
                    userId, sourceDocumentId, command.targetDocumentId(), saved.getId());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=document_relation_create_실패 userId={}, sourceDocumentId={}, targetDocumentId={}, reason={}",
                    userId, sourceDocumentId, command.targetDocumentId(), e.getMessage(), e);
            throw e;
        }
    }
}
