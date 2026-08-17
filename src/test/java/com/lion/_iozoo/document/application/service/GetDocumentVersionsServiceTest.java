package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.DocumentVersion;
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
class GetDocumentVersionsServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentVersionsPort loadDocumentVersionsPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocumentVersionsService sut() {
        return new GetDocumentVersionsService(loadDocumentPort, loadDocumentVersionsPort, teamPermissionChecker);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.OFFICIAL).restricted(false)
                .build();
    }

    @Test
    void 버전_이력을_조회한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        List<DocumentVersion> versions = List.of(
                DocumentVersion.builder().id(1L).documentId(100L).versionNo(1).content("초안").docPrId(null).createdAt(LocalDateTime.now()).build(),
                DocumentVersion.builder().id(2L).documentId(100L).versionNo(2).content("변경본").docPrId(5L).createdAt(LocalDateTime.now()).build()
        );
        when(loadDocumentVersionsPort.loadByDocumentId(100L)).thenReturn(versions);

        List<DocumentVersion> result = sut().getVersions(1L, 100L);

        assertThat(result).hasSize(2);
        assertThat(result.get(1).getDocPrId()).isEqualTo(5L);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getVersions(1L, 100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getVersions(99L, 100L))
                .isInstanceOf(ForbiddenException.class);
    }
}
