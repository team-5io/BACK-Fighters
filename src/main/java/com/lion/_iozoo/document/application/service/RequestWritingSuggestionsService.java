package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RequestWritingSuggestionsCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.RequestWritingSuggestionsPort;
import com.lion._iozoo.document.application.result.WritingSuggestionResult;
import com.lion._iozoo.document.application.usecase.RequestWritingSuggestionsUseCase;
import com.lion._iozoo.document.domain.Document;
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
public class RequestWritingSuggestionsService implements RequestWritingSuggestionsUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;
    private final RequestWritingSuggestionsPort requestWritingSuggestionsPort;

    @Override
    @Transactional(readOnly = true)
    public List<WritingSuggestionResult> request(Long userId, Long documentId, RequestWritingSuggestionsCommand command) {
        log.info("event=writing_suggestions_request_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            List<WritingSuggestionResult> suggestions = requestWritingSuggestionsPort.requestSuggestions(
                    documentId, command.content(), command.cursorContext());

            log.info("event=writing_suggestions_request_완료 userId={}, documentId={}, count={}",
                    userId, documentId, suggestions.size());
            return suggestions;
        } catch (RuntimeException e) {
            log.warn("event=writing_suggestions_request_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
