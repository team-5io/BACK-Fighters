package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.exception.DocumentAccessDeniedException;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
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
class GetDocumentServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentRaciPort loadDocumentRaciPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocumentService sut() {
        return new GetDocumentService(loadDocumentPort, loadDocumentRaciPort, teamPermissionChecker);
    }

    private Document document(boolean restricted) {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.OFFICIAL).restricted(restricted)
                .build();
    }

    @Test
    void 문서를_단건_조회한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(false)));

        Document result = sut().getById(1L, 100L);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("제목");
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getById(1L, 100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(false)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getById(99L, 100L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void restricted_문서에_역할이_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(true)));

        assertThatThrownBy(() -> sut().getById(99L, 100L))
                .isInstanceOf(DocumentAccessDeniedException.class);
    }

    @Test
    void restricted_문서라도_RACI_C면_조회한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(true)));
        when(loadDocumentRaciPort.loadByDocumentId(100L)).thenReturn(List.of(
                new DocumentRaciEntry(99L, RaciRole.C, 10L, LocalDateTime.now())
        ));

        Document result = sut().getById(99L, 100L);

        assertThat(result.getId()).isEqualTo(100L);
    }
}
