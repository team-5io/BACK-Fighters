package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RaciAssignmentCommand;
import com.lion._iozoo.document.application.command.SetDocumentRaciCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.ReplaceDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.usecase.SetDocumentRaciUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.DocumentRaciDuplicateUserException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetDocumentRaciService implements SetDocumentRaciUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final ReplaceDocumentRaciPort replaceDocumentRaciPort;
    private final SaveDocumentPort saveDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public List<DocumentRaciEntry> setRaci(Long userId, SetDocumentRaciCommand command) {
        log.info("event=document_raci_set_시작 userId={}, documentId={}", userId, command.documentId());

        try {
            Document document = loadDocumentPort.loadById(command.documentId())
                    .orElseThrow(() -> new DocumentNotFoundException(command.documentId()));

            // RACI 지정/변경은 팀 관리자만 가능 (기능명세서 "RACI 역할 지정/변경" 권한 기준)
            teamPermissionChecker.requireAdmin(document.getTeamId(), userId);

            List<RaciAssignmentCommand> assignments = command.assignments();

            Set<Long> distinctMemberIds = assignments.stream()
                    .map(RaciAssignmentCommand::memberId)
                    .collect(Collectors.toSet());
            if (distinctMemberIds.size() != assignments.size()) {
                throw new DocumentRaciDuplicateUserException(command.documentId());
            }

            LocalDateTime now = LocalDateTime.now();
            List<DocumentRaciEntry> newEntries = assignments.stream()
                    .map(a -> {
                        Long assigneeUserId = teamPermissionChecker.resolveUserId(document.getTeamId(), a.memberId());
                        return new DocumentRaciEntry(assigneeUserId, a.role(), userId, now);
                    })
                    .toList();

            List<DocumentRaciEntry> saved = replaceDocumentRaciPort.replaceAll(command.documentId(), newEntries);

            document.applyRaciAssignment(!saved.isEmpty());
            saveDocumentPort.save(document);

            log.info("event=document_raci_set_완료 userId={}, documentId={}, count={}",
                    userId, command.documentId(), saved.size());
            return saved;
        } catch (RuntimeException e) {
            log.warn("event=document_raci_set_실패 userId={}, documentId={}, reason={}",
                    userId, command.documentId(), e.getMessage(), e);
            throw e;
        }
    }
}
