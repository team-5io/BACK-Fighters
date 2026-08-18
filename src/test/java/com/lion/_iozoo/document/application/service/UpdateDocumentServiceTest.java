package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotDraftException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateDocumentServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private SaveDocumentPort saveDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private UpdateDocumentService sut() {
        return new UpdateDocumentService(loadDocumentPort, saveDocumentPort, teamPermissionChecker);
    }

    private Document draft(Long authorId) {
        return Document.builder()
                .id(100L).teamId(1L).authorId(authorId)
                .title("원본 제목").content("원본 내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    @Test
    void 작성자_본인이_초안을_수정한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(draft(10L)));
        when(saveDocumentPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Document result = sut().update(10L, 100L, new UpdateDocumentCommand("새 제목", List.of()));

        assertThat(result.getTitle()).isEqualTo("새 제목");
        verify(teamPermissionChecker).requireMember(1L, 10L);
    }

    @Test
    void 작성자가_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(draft(10L)));

        assertThatThrownBy(() -> sut().update(99L, 100L, new UpdateDocumentCommand("새 제목", List.of())))
                .isInstanceOf(DocumentAccessDeniedException.class);

        verify(saveDocumentPort, never()).save(any());
    }

    @Test
    void OFFICIAL_문서면_예외() {
        Document official = Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.OFFICIAL).restricted(false)
                .build();
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(official));

        assertThatThrownBy(() -> sut().update(10L, 100L, new UpdateDocumentCommand("새 제목", List.of())))
                .isInstanceOf(DocumentNotDraftException.class);

        verify(saveDocumentPort, never()).save(any());
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().update(10L, 100L, new UpdateDocumentCommand("새 제목", List.of())))
                .isInstanceOf(DocumentNotFoundException.class);
    }
}
