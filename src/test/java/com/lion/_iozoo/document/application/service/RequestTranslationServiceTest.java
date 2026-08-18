package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RequestTranslationCommand;
import com.lion._iozoo.document.application.port.out.LoadCachedTranslationPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.RequestTranslationPort;
import com.lion._iozoo.document.application.port.out.SaveTranslationPort;
import com.lion._iozoo.document.application.result.RequestTranslationResult;
import com.lion._iozoo.document.application.result.TranslationGatewayResult;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.TranslationFailedException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTranslationServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadCachedTranslationPort loadCachedTranslationPort;
    @Mock
    private SaveTranslationPort saveTranslationPort;
    @Mock
    private RequestTranslationPort requestTranslationPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private RequestTranslationService sut() {
        return new RequestTranslationService(
                loadDocumentPort, loadCachedTranslationPort, saveTranslationPort, requestTranslationPort, teamPermissionChecker);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    private RequestTranslationCommand command() {
        return new RequestTranslationCommand("원문", "ko", "en");
    }

    @Test
    void 캐시가_있으면_AI_Gateway를_호출하지_않고_캐시를_반환한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        Translation cached = Translation.builder()
                .id(1L).documentId(100L).sourceLanguage("ko").targetLanguage("en")
                .translatedContent("cached content").preservedTerms(List.of("RACI"))
                .createdAt(LocalDateTime.now())
                .build();
        when(loadCachedTranslationPort.loadByDocumentIdAndTargetLanguage(100L, "en")).thenReturn(Optional.of(cached));

        RequestTranslationResult result = sut().translate(30L, 100L, command());

        assertThat(result.cached()).isTrue();
        assertThat(result.translation().getTranslatedContent()).isEqualTo("cached content");
        verify(requestTranslationPort, never()).requestTranslation(any(), any(), any(), any());
        verify(saveTranslationPort, never()).save(any());
    }

    @Test
    void 캐시가_없으면_AI_Gateway를_호출하고_결과를_저장한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadCachedTranslationPort.loadByDocumentIdAndTargetLanguage(100L, "en")).thenReturn(Optional.empty());
        when(requestTranslationPort.requestTranslation(100L, "원문", "ko", "en"))
                .thenReturn(new TranslationGatewayResult("translated", List.of("Doc PR", "RACI")));
        when(saveTranslationPort.save(any())).thenAnswer(invocation -> {
            Translation arg = invocation.getArgument(0);
            return Translation.builder()
                    .id(1L).documentId(arg.getDocumentId()).sourceLanguage(arg.getSourceLanguage())
                    .targetLanguage(arg.getTargetLanguage()).translatedContent(arg.getTranslatedContent())
                    .preservedTerms(arg.getPreservedTerms()).createdAt(arg.getCreatedAt())
                    .build();
        });

        RequestTranslationResult result = sut().translate(30L, 100L, command());

        assertThat(result.cached()).isFalse();
        assertThat(result.translation().getTranslatedContent()).isEqualTo("translated");
        assertThat(result.translation().getPreservedTerms()).containsExactly("Doc PR", "RACI");
    }

    @Test
    void AI_Gateway_호출이_실패하면_예외가_전파된다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadCachedTranslationPort.loadByDocumentIdAndTargetLanguage(100L, "en")).thenReturn(Optional.empty());
        when(requestTranslationPort.requestTranslation(100L, "원문", "ko", "en"))
                .thenThrow(new TranslationFailedException(100L, null));

        assertThatThrownBy(() -> sut().translate(30L, 100L, command()))
                .isInstanceOf(TranslationFailedException.class);

        verify(saveTranslationPort, never()).save(any());
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().translate(30L, 100L, command()))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().translate(99L, 100L, command()))
                .isInstanceOf(ForbiddenException.class);
    }
}
