package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.DeleteDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteDocumentServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private DeleteDocumentPort deleteDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private DeleteDocumentService sut() {
        return new DeleteDocumentService(loadDocumentPort, deleteDocumentPort, teamPermissionChecker);
    }

    private Document document(Long authorId, DocumentStatus status) {
        return Document.builder()
                .id(100L).teamId(1L).authorId(authorId)
                .title("제목").content("내용")
                .status(status).restricted(false)
                .build();
    }

    @Test
    void 작성자_본인이_삭제한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, DocumentStatus.OFFICIAL)));

        sut().delete(10L, 100L);

        verify(teamPermissionChecker, never()).requireAdmin(1L, 10L);
        verify(deleteDocumentPort).deleteById(100L);
    }

    @Test
    void 팀_관리자는_작성자가_아니어도_삭제한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, DocumentStatus.OFFICIAL)));

        sut().delete(99L, 100L);

        verify(teamPermissionChecker).requireAdmin(1L, 99L);
        verify(deleteDocumentPort).deleteById(100L);
    }

    @Test
    void 작성자도_관리자도_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, DocumentStatus.OFFICIAL)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 11L);

        assertThatThrownBy(() -> sut().delete(11L, 100L))
                .isInstanceOf(ForbiddenException.class);

        verify(deleteDocumentPort, never()).deleteById(100L);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().delete(10L, 100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void DRAFT_문서도_작성자면_삭제된다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, DocumentStatus.DRAFT)));

        assertThatCode(() -> sut().delete(10L, 100L)).doesNotThrowAnyException();

        verify(deleteDocumentPort).deleteById(100L);
    }
}
