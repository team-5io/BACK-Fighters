package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentQueryAdapterForDocPrTest {

    @Mock
    private DocumentJpaRepository documentJpaRepository;

    private DocumentQueryAdapterForDocPr sut() {
        return new DocumentQueryAdapterForDocPr(documentJpaRepository);
    }

    private DocumentEntity documentEntity() {
        return DocumentEntity.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    @Test
    void 존재하는_문서면_요약정보를_반환한다() {
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(documentEntity()));

        Optional<DocumentSummary> result = sut().loadSummary(100L);

        assertThat(result).isPresent();
        assertThat(result.get().teamId()).isEqualTo(1L);
        assertThat(result.get().authorId()).isEqualTo(10L);
        assertThat(result.get().draft()).isTrue();
    }

    @Test
    void 삭제된_문서면_빈_값을_반환한다() {
        DocumentEntity deleted = documentEntity();
        deleted.softDelete(LocalDateTime.now());
        when(documentJpaRepository.findById(100L)).thenReturn(Optional.of(deleted));

        Optional<DocumentSummary> result = sut().loadSummary(100L);

        assertThat(result).isEmpty();
    }
}
