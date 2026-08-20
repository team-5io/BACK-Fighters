package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.result.DocumentWithRelationsResult;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.document.domain.RelationType;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDocumentRelationsServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentRelationsPort loadDocumentRelationsPort;
    @Mock
    private LoadDocumentRaciPort loadDocumentRaciPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocumentRelationsService sut() {
        return new GetDocumentRelationsService(loadDocumentPort, loadDocumentRelationsPort, loadDocumentRaciPort, teamPermissionChecker);
    }

    private Document document(Long id, Long authorId, boolean restricted) {
        return Document.builder()
                .id(id).teamId(1L).authorId(authorId)
                .title("문서" + id).content("내용")
                .status(DocumentStatus.DRAFT).restricted(restricted)
                .build();
    }

    private DocumentRelation relation(Long id, Long source, Long target) {
        return DocumentRelation.builder()
                .id(id).sourceDocumentId(source).targetDocumentId(target)
                .relationType(RelationType.REFERENCE).createdAt(LocalDateTime.now())
                .build();
    }

    private Page<Document> pageOf(Document... documents) {
        return new PageImpl<>(List.of(documents), PageRequest.of(0, 20), documents.length);
    }

    @Test
    void 팀의_문서_목록을_관계와_함께_조회한다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(
                relation(1L, 100L, 200L),
                relation(2L, 300L, 100L)
        ));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 1L, false)));
        when(loadDocumentPort.loadById(300L)).thenReturn(Optional.of(document(300L, 1L, false)));

        Page<DocumentWithRelationsResult> result = sut().explore(1L, 1L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).relations()).hasSize(2);
        assertThat(result.getContent().get(0).relations()).extracting(r -> r.direction())
                .containsExactlyInAnyOrder(RelationDirection.OUTGOING, RelationDirection.INCOMING);
    }

    @Test
    void 관계가_없는_독립_문서는_relations가_null이다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(101L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(101L)).thenReturn(List.of());

        Page<DocumentWithRelationsResult> result = sut().explore(1L, 1L, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).relations()).isNull();
    }

    @Test
    void restricted_이웃_문서는_작성자가_아니면_숨긴다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(1L, 100L, 200L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 99L, true)));

        Page<DocumentWithRelationsResult> result = sut().explore(1L, 1L, PageRequest.of(0, 20));

        assertThat(result.getContent().get(0).relations()).isNull();
    }

    @Test
    void restricted_이웃_문서라도_작성자_본인이면_보인다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(1L, 100L, 200L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 1L, true)));

        Page<DocumentWithRelationsResult> result = sut().explore(1L, 1L, PageRequest.of(0, 20));

        assertThat(result.getContent().get(0).relations()).hasSize(1);
    }

    @Test
    void 팀원이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().explore(99L, 1L, PageRequest.of(0, 20)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void restricted_이웃_문서라도_RACI_R_A_C면_보인다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(1L, 100L, 200L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 99L, true)));
        when(loadDocumentRaciPort.loadByDocumentId(200L)).thenReturn(List.of(
                new DocumentRaciEntry(1L, RaciRole.C, 99L, LocalDateTime.now())
        ));

        Page<DocumentWithRelationsResult> result = sut().explore(1L, 1L, PageRequest.of(0, 20));

        assertThat(result.getContent().get(0).relations()).hasSize(1);
    }

    @Test
    void restricted_이웃_문서에서_I는_초안이면_숨기고_공식문서면_보인다() {
        when(loadDocumentPort.loadByTeamId(eq(1L), eq(1L), any())).thenReturn(pageOf(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(1L, 100L, 200L)));
        Document draftNeighbor = document(200L, 99L, true);
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(draftNeighbor));
        when(loadDocumentRaciPort.loadByDocumentId(200L)).thenReturn(List.of(
                new DocumentRaciEntry(1L, RaciRole.I, 99L, LocalDateTime.now())
        ));

        assertThat(sut().explore(1L, 1L, PageRequest.of(0, 20)).getContent().get(0).relations()).isNull();

        Document officialNeighbor = Document.builder()
                .id(200L).teamId(1L).authorId(99L)
                .title("문서200").content("내용")
                .status(DocumentStatus.OFFICIAL).restricted(true)
                .build();
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(officialNeighbor));

        assertThat(sut().explore(1L, 1L, PageRequest.of(0, 20)).getContent().get(0).relations()).hasSize(1);
    }
}
