package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadTranslationPort;
import com.lion._iozoo.document.application.usecase.GetTranslationUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.TranslationNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetTranslationService implements GetTranslationUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadTranslationPort loadTranslationPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Translation getById(Long userId, Long documentId, Long translationId) {
        log.info("event=translation_get_시작 userId={}, documentId={}, translationId={}", userId, documentId, translationId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            Translation translation = loadTranslationPort.loadById(translationId)
                    .filter(t -> t.getDocumentId().equals(documentId))
                    .orElseThrow(() -> new TranslationNotFoundException(translationId));

            log.info("event=translation_get_완료 userId={}, documentId={}, translationId={}", userId, documentId, translationId);
            return translation;
        } catch (RuntimeException e) {
            log.warn("event=translation_get_실패 userId={}, documentId={}, translationId={}, reason={}",
                    userId, documentId, translationId, e.getMessage(), e);
            throw e;
        }
    }
}
