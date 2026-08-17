package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.application.result.DocumentImpactResult;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RelationType;
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
class AnalyzeDocumentImpactServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentRelationsPort loadDocumentRelationsPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private AnalyzeDocumentImpactService sut() {
        return new AnalyzeDocumentImpactService(loadDocumentPort, loadDocumentRelationsPort, teamPermissionChecker);
    }

    private Document document(Long id, Long authorId, boolean restricted) {
        return Document.builder()
                .id(id).teamId(1L).authorId(authorId)
                .title("문서" + id).content("내용")
                .status(DocumentStatus.DRAFT).restricted(restricted)
                .build();
    }

    private DocumentRelation relation(Long source, Long target) {
        return DocumentRelation.builder()
                .id(source * 1000 + target).sourceDocumentId(source).targetDocumentId(target)
                .relationType(RelationType.REFERENCE).createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 다단계로_연결된_문서를_hop수와_함께_조회한다() {
        // A(100) -> B(200) -> C(300)
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(100L, 200L)));
        when(loadDocumentRelationsPort.loadByDocumentId(200L)).thenReturn(List.of(relation(100L, 200L), relation(200L, 300L)));
        when(loadDocumentRelationsPort.loadByDocumentId(300L)).thenReturn(List.of(relation(200L, 300L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 1L, false)));
        when(loadDocumentPort.loadById(300L)).thenReturn(Optional.of(document(300L, 1L, false)));

        List<DocumentImpactResult> result = sut().analyze(1L, 100L);

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(r -> r.documentId().equals(200L) && r.depth() == 1);
        assertThat(result).anyMatch(r -> r.documentId().equals(300L) && r.depth() == 2);
    }

    @Test
    void restricted_문서는_숨기고_그_너머로는_탐색하지_않는다() {
        // A(100) -> B(200, restricted, 타인 작성) -> C(300)
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(100L, 200L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 99L, true)));

        List<DocumentImpactResult> result = sut().analyze(1L, 100L);

        assertThat(result).isEmpty();
    }

    @Test
    void 순환_그래프에서도_무한루프_없이_종료한다() {
        // A(100) -> B(200) -> C(300) -> A(100)
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L, 1L, false)));
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation(100L, 200L)));
        when(loadDocumentRelationsPort.loadByDocumentId(200L)).thenReturn(List.of(relation(100L, 200L), relation(200L, 300L)));
        when(loadDocumentRelationsPort.loadByDocumentId(300L)).thenReturn(List.of(relation(200L, 300L), relation(300L, 100L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 1L, false)));
        when(loadDocumentPort.loadById(300L)).thenReturn(Optional.of(document(300L, 1L, false)));

        List<DocumentImpactResult> result = sut().analyze(1L, 100L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DocumentImpactResult::documentId).containsExactlyInAnyOrder(200L, 300L);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().analyze(1L, 100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L, 1L, false)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().analyze(99L, 100L))
                .isInstanceOf(ForbiddenException.class);
    }
}
