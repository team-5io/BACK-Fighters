package com.lion._iozoo.document.infrastructure.persistence;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentMergeAdapterForDocPrTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private SaveDocumentPort saveDocumentPort;

    private DocumentMergeAdapterForDocPr sut() {
        return new DocumentMergeAdapterForDocPr(loadDocumentPort, saveDocumentPort);
    }

    // 회귀 테스트: proposedContent(평문 설명)를 블록 JSON으로 재파싱하지 않고,
    // 문서가 이미 갖고 있는 블록을 그대로 OFFICIAL로 승격한다.
    @Test
    void 문서가_이미_갖고있는_블록을_그대로_OFFICIAL로_승격한다() {
        Block block = Block.builder().id("b1").type("paragraph").content("최신 내용").build();
        Document document = Document.builder()
                .id(100L).teamId(1L).authorId(10L).title("제목")
                .blocks(List.of(block))
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document));

        sut().markOfficial(100L);

        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        verify(saveDocumentPort).save(captor.capture());
        Document saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(DocumentStatus.OFFICIAL);
        assertThat(saved.getBlocks()).containsExactly(block);
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().markOfficial(100L))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(saveDocumentPort, org.mockito.Mockito.never()).save(any());
    }
}
