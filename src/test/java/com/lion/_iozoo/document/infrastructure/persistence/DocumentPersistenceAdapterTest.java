package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.infrastructure.persistence.entity.BlockEntity;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.BlockJpaRepository;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentPersistenceAdapterTest {

    @Mock
    private DocumentJpaRepository documentJpaRepository;
    @Mock
    private BlockJpaRepository blockJpaRepository;
    @Mock
    private DocumentMapper documentMapper;

    private DocumentPersistenceAdapter sut() {
        return new DocumentPersistenceAdapter(documentJpaRepository, blockJpaRepository, documentMapper);
    }

    private DocumentEntity documentEntity() {
        return DocumentEntity.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("부모 내용\n자식 내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    @Test
    void 검색_키워드의_LIKE_와일드카드를_이스케이프해서_전달한다() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<DocumentEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        ArgumentCaptor<String> patternCaptor = ArgumentCaptor.forClass(String.class);
        when(documentJpaRepository.searchByKeyword(eq(1L), eq(10L), patternCaptor.capture(), any()))
                .thenReturn(emptyPage);

        sut().searchByKeyword(1L, 10L, "50%_off", pageable);

        assertThat(patternCaptor.getValue()).isEqualTo("%50\\%\\_off%");
    }

    @Test
    void 삭제_대상이_없으면_DocumentNotFoundException을_던진다() {
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().deleteById(100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 이미_삭제된_문서를_다시_삭제하면_DocumentNotFoundException을_던진다() {
        DocumentEntity alreadyDeleted = documentEntity();
        alreadyDeleted.softDelete(LocalDateTime.now());
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(alreadyDeleted));

        assertThatThrownBy(() -> sut().deleteById(100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 문서_삭제는_하드_삭제_대신_deletedAt만_채운다() {
        DocumentEntity entity = documentEntity();
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(entity));

        sut().deleteById(100L);

        assertThat(entity.getDeletedAt()).isNotNull();
        verify(documentJpaRepository, never()).deleteById(any());
        verify(documentJpaRepository, never()).delete(any());
        verify(blockJpaRepository, never()).deleteByDocumentId(any());
    }

    @Test
    void 삭제된_문서는_단건조회에서_제외된다() {
        DocumentEntity deleted = documentEntity();
        deleted.softDelete(LocalDateTime.now());
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(deleted));

        Optional<Document> result = sut().loadById(100L);

        assertThat(result).isEmpty();
    }

    @Test
    void 저장시_기존_블록을_지우고_트리를_평탄화해서_다시_저장한다() {
        when(documentMapper.toEntity(any())).thenReturn(documentEntity());
        when(documentJpaRepository.save(any())).thenReturn(documentEntity());

        Block child = Block.builder().id("b2").type("paragraph").content("자식 내용").build();
        Block parent = Block.builder().id("b1").type("paragraph").content("부모 내용")
                .children(List.of(child)).build();
        Document document = Document.builder()
                .teamId(1L).authorId(10L).title("제목")
                .blocks(List.of(parent))
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();

        sut().save(document);

        verify(blockJpaRepository).deleteByDocumentId(100L);
        ArgumentCaptor<List<BlockEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(blockJpaRepository).saveAll(captor.capture());
        List<BlockEntity> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getId()).isEqualTo("b1");
        assertThat(saved.get(0).getParentBlockId()).isNull();
        assertThat(saved.get(1).getId()).isEqualTo("b2");
        assertThat(saved.get(1).getParentBlockId()).isEqualTo("b1");
    }

    @Test
    void 조회시_평탄화된_블록들을_트리로_재구성한다() {
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(documentEntity()));
        BlockEntity parent = BlockEntity.builder()
                .id("b1").documentId(100L).parentBlockId(null).sortOrder(0)
                .type("paragraph").content("부모 내용").build();
        BlockEntity child = BlockEntity.builder()
                .id("b2").documentId(100L).parentBlockId("b1").sortOrder(0)
                .type("paragraph").content("자식 내용").build();
        when(blockJpaRepository.findByDocumentIdOrderBySortOrderAsc(100L)).thenReturn(List.of(parent, child));

        Document result = sut().loadById(100L).orElseThrow();

        assertThat(result.getBlocks()).hasSize(1);
        Block root = result.getBlocks().get(0);
        assertThat(root.getId()).isEqualTo("b1");
        assertThat(root.getChildren()).hasSize(1);
        assertThat(root.getChildren().get(0).getId()).isEqualTo("b2");
    }
}
