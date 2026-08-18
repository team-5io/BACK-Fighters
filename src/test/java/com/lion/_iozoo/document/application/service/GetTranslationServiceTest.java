package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadTranslationPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.TranslationNotFoundException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTranslationServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadTranslationPort loadTranslationPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetTranslationService sut() {
        return new GetTranslationService(loadDocumentPort, loadTranslationPort, teamPermissionChecker);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    private Translation translation(Long documentId) {
        return Translation.builder()
                .id(1L).documentId(documentId).sourceLanguage("ko").targetLanguage("en")
                .translatedContent("translated").preservedTerms(List.of("RACI"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 저장된_번역_결과를_조회한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadTranslationPort.loadById(1L)).thenReturn(Optional.of(translation(100L)));

        Translation result = sut().getById(30L, 100L, 1L);

        assertThat(result.getTranslatedContent()).isEqualTo("translated");
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getById(30L, 100L, 1L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 번역_결과가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadTranslationPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getById(30L, 100L, 1L))
                .isInstanceOf(TranslationNotFoundException.class);
    }

    @Test
    void 다른_문서의_번역_결과면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadTranslationPort.loadById(1L)).thenReturn(Optional.of(translation(999L)));

        assertThatThrownBy(() -> sut().getById(30L, 100L, 1L))
                .isInstanceOf(TranslationNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getById(99L, 100L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
}
