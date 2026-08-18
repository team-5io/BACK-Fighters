package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RequestTranslationCommand;
import com.lion._iozoo.document.application.port.out.LoadCachedTranslationPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.RequestTranslationPort;
import com.lion._iozoo.document.application.port.out.SaveTranslationPort;
import com.lion._iozoo.document.application.result.RequestTranslationResult;
import com.lion._iozoo.document.application.result.TranslationGatewayResult;
import com.lion._iozoo.document.application.usecase.RequestTranslationUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestTranslationService implements RequestTranslationUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final LoadCachedTranslationPort loadCachedTranslationPort;
    private final SaveTranslationPort saveTranslationPort;
    private final RequestTranslationPort requestTranslationPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public RequestTranslationResult translate(Long userId, Long documentId, RequestTranslationCommand command) {
        log.info("event=translation_request_시작 userId={}, documentId={}, blockId={}, targetLanguage={}",
                userId, documentId, command.blockId(), command.targetLanguage());

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            Optional<Translation> cached = loadCachedTranslationPort
                    .loadByDocumentIdAndBlockIdAndTargetLanguage(documentId, command.blockId(), command.targetLanguage());
            if (cached.isPresent()) {
                log.info("event=translation_request_완료 userId={}, documentId={}, cached=true", userId, documentId);
                return new RequestTranslationResult(cached.get(), true);
            }

            TranslationGatewayResult gatewayResult = requestTranslationPort.requestTranslation(
                    documentId, command.blockId(), command.content(), command.sourceLanguage(), command.targetLanguage());

            Translation saved = saveTranslationPort.save(Translation.builder()
                    .documentId(documentId)
                    .blockId(command.blockId())
                    .sourceLanguage(command.sourceLanguage())
                    .targetLanguage(command.targetLanguage())
                    .translatedContent(gatewayResult.translatedContent())
                    .preservedTerms(gatewayResult.preservedTerms())
                    .createdAt(LocalDateTime.now())
                    .build());

            log.info("event=translation_request_완료 userId={}, documentId={}, cached=false", userId, documentId);
            return new RequestTranslationResult(saved, false);
        } catch (RuntimeException e) {
            log.warn("event=translation_request_실패 userId={}, documentId={}, reason={}", userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
