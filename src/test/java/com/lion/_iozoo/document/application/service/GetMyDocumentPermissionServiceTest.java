package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RaciRole;
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
class GetMyDocumentPermissionServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentRaciPort loadDocumentRaciPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetMyDocumentPermissionService sut() {
        return new GetMyDocumentPermissionService(loadDocumentPort, loadDocumentRaciPort, teamPermissionChecker);
    }

    private Document document(Long authorId, boolean restricted, DocumentStatus status) {
        return Document.builder()
                .id(100L).teamId(1L).authorId(authorId)
                .title("제목").content("내용")
                .status(status).restricted(restricted)
                .build();
    }

    @Test
    void 작성자는_FULL을_반환한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, true, DocumentStatus.DRAFT)));
        when(loadDocumentRaciPort.loadByDocumentId(100L)).thenReturn(List.of());

        MyDocumentPermissionResult result = sut().getMyPermission(10L, 100L);

        assertThat(result.accessLevel()).isEqualTo(DocumentAccessLevel.FULL);
        assertThat(result.isAuthor()).isTrue();
        assertThat(result.canViewDocPr()).isTrue();
    }

    @Test
    void I_역할은_초안이면_NONE_공식문서면_OFFICIAL_ONLY다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, true, DocumentStatus.DRAFT)));
        when(loadDocumentRaciPort.loadByDocumentId(100L)).thenReturn(List.of(
                new DocumentRaciEntry(20L, RaciRole.I, 10L, LocalDateTime.now())
        ));

        MyDocumentPermissionResult draftResult = sut().getMyPermission(20L, 100L);
        assertThat(draftResult.accessLevel()).isEqualTo(DocumentAccessLevel.NONE);
        assertThat(draftResult.canViewDocPr()).isFalse();

        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, true, DocumentStatus.OFFICIAL)));

        MyDocumentPermissionResult officialResult = sut().getMyPermission(20L, 100L);
        assertThat(officialResult.accessLevel()).isEqualTo(DocumentAccessLevel.OFFICIAL_ONLY);
        assertThat(officialResult.canViewDocPr()).isFalse();
        assertThat(officialResult.role()).isEqualTo(RaciRole.I);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getMyPermission(10L, 100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(10L, false, DocumentStatus.DRAFT)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getMyPermission(99L, 100L))
                .isInstanceOf(ForbiddenException.class);
    }
}
