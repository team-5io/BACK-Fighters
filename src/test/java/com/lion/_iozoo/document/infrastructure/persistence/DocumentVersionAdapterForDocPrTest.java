package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentVersionsPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentVersionPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentVersionAdapterForDocPrTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private LoadDocumentVersionsPort loadDocumentVersionsPort;
    @Mock
    private SaveDocumentVersionPort saveDocumentVersionPort;

    private DocumentVersionAdapterForDocPr sut() {
        return new DocumentVersionAdapterForDocPr(loadDocumentPort, loadDocumentVersionsPort, saveDocumentVersionPort);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L).title("제목")
                .content("최신 내용")
                .status(DocumentStatus.OFFICIAL).restricted(false)
                .build();
    }

    // 회귀 테스트: proposedContent(평문 설명)가 아니라 문서의 평문 캐시(content)를 버전 스냅샷으로 저장한다.
    @Test
    void 문서의_평문_캐시를_버전_스냅샷으로_저장한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(loadDocumentVersionsPort.countByDocumentId(100L)).thenReturn(1);

        sut().record(100L, 5L);

        ArgumentCaptor<DocumentVersion> captor = ArgumentCaptor.forClass(DocumentVersion.class);
        verify(saveDocumentVersionPort).save(captor.capture());
        DocumentVersion saved = captor.getValue();
        assertThat(saved.getDocumentId()).isEqualTo(100L);
        assertThat(saved.getVersionNo()).isEqualTo(2);
        assertThat(saved.getContent()).isEqualTo("최신 내용");
        assertThat(saved.getDocPrId()).isEqualTo(5L);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().record(100L, 5L))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(saveDocumentVersionPort, org.mockito.Mockito.never()).save(any());
    }
}
