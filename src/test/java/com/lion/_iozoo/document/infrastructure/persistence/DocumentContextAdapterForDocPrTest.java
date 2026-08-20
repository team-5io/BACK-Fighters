package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.DocumentBlockContent;
import com.lion._iozoo.docpr.application.port.out.RelatedDocumentContent;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentRelationsPort;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RelationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentContextAdapterForDocPrTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentRelationsPort loadDocumentRelationsPort;
    @Mock
    private LoadDocumentRaciPort loadDocumentRaciPort;

    private DocumentContextAdapterForDocPr sut() {
        return new DocumentContextAdapterForDocPr(loadDocumentPort, loadDocumentRelationsPort, loadDocumentRaciPort);
    }

    private Document document(Long id, Long authorId, boolean restricted, List<Block> blocks) {
        return Document.builder()
                .id(id).teamId(1L).authorId(authorId)
                .title("문서" + id).content("내용").blocks(blocks)
                .status(DocumentStatus.DRAFT).restricted(restricted)
                .build();
    }

    private Block block(String id, String content, List<Block> children) {
        return Block.builder().id(id).type("paragraph").content(content).children(children).build();
    }

    @Test
    void 블록을_재귀적으로_평탄화하고_본문없는_블록은_제외한다() {
        List<Block> blocks = List.of(
                block("b1", "첫 블록", List.of(block("b1-1", "자식 블록", List.of()))),
                block("b2", null, List.of()),
                block("b3", "  ", List.of()));
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L, 1L, false, blocks)));

        List<DocumentBlockContent> result = sut().loadFlattenedBlocks(100L);

        assertThat(result).containsExactly(
                new DocumentBlockContent("b1", "첫 블록"),
                new DocumentBlockContent("b1-1", "자식 블록"));
    }

    @Test
    void 문서가_없으면_빈_목록을_반환한다() {
        when(loadDocumentPort.loadById(999L)).thenReturn(Optional.empty());

        assertThat(sut().loadFlattenedBlocks(999L)).isEmpty();
    }

    @Test
    void restricted_이웃_문서는_작성자가_아니면_제외한다() {
        DocumentRelation relation = DocumentRelation.builder()
                .id(1L).sourceDocumentId(100L).targetDocumentId(200L)
                .relationType(RelationType.REFERENCE).createdAt(LocalDateTime.now())
                .build();
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 99L, true, List.of())));
        when(loadDocumentRaciPort.loadByDocumentId(200L)).thenReturn(List.of());

        List<RelatedDocumentContent> result = sut().loadVisibleRelatedDocuments(100L, 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void 공개_이웃_문서는_본문과_방향을_포함해_반환한다() {
        DocumentRelation relation = DocumentRelation.builder()
                .id(1L).sourceDocumentId(100L).targetDocumentId(200L)
                .relationType(RelationType.REFERENCE).createdAt(LocalDateTime.now())
                .build();
        when(loadDocumentRelationsPort.loadByDocumentId(100L)).thenReturn(List.of(relation));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L, 1L, false, List.of())));

        List<RelatedDocumentContent> result = sut().loadVisibleRelatedDocuments(100L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).documentId()).isEqualTo(200L);
        assertThat(result.get(0).relationType()).isEqualTo("REFERENCE");
        assertThat(result.get(0).direction()).isEqualTo("OUTGOING");
    }
}
