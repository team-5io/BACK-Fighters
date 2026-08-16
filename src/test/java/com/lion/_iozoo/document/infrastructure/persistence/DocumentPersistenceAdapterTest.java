package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.document.infrastructure.persistence.mapper.DocumentMapper;
import com.lion._iozoo.document.infrastructure.persistence.repository.DocumentJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentPersistenceAdapterTest {

    @Mock
    private DocumentJpaRepository documentJpaRepository;
    @Mock
    private DocumentMapper documentMapper;

    private DocumentPersistenceAdapter sut() {
        return new DocumentPersistenceAdapter(documentJpaRepository, documentMapper);
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
    void 삭제_대상이_이미_없으면_DocumentNotFoundException으로_변환한다() {
        doThrow(new EmptyResultDataAccessException(1)).when(documentJpaRepository).deleteById(100L);

        assertThatThrownBy(() -> sut().deleteById(100L))
                .isInstanceOf(DocumentNotFoundException.class);
    }
}
