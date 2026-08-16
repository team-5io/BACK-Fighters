package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ResubmitDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotRejectedException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotRequesterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResubmitDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private SaveDocPrPort saveDocPrPort;
    @Mock
    private SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;

    private ResubmitDocPrService sut() {
        return new ResubmitDocPrService(loadDocPrPort, saveDocPrPort, saveDocPrStatusHistoryPort);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("원래 내용").status(status)
                .build();
    }

    @Test
    void 요청자가_반려된_PR을_재제출한다() {
        DocPr docPr = docPr(DocPrStatus.REJECTED);
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr));
        when(saveDocPrPort.save(docPr)).thenReturn(docPr);

        DocPr result = sut().resubmit(10L, new ResubmitDocPrCommand(1L, "수정된 내용"));

        assertThat(result.getStatus()).isEqualTo(DocPrStatus.RESUBMITTED);
        assertThat(result.getProposedContent()).isEqualTo("수정된 내용");
        verify(saveDocPrStatusHistoryPort).save(eq(1L), eq(DocPrStatus.REJECTED), eq(DocPrStatus.RESUBMITTED), eq(10L), isNull());
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().resubmit(10L, new ResubmitDocPrCommand(1L, "수정된 내용")))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 요청자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.REJECTED)));

        assertThatThrownBy(() -> sut().resubmit(99L, new ResubmitDocPrCommand(1L, "수정된 내용")))
                .isInstanceOf(DocPrNotRequesterException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 반려_상태가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));

        assertThatThrownBy(() -> sut().resubmit(10L, new ResubmitDocPrCommand(1L, "수정된 내용")))
                .isInstanceOf(DocPrNotRejectedException.class);

        verify(saveDocPrPort, never()).save(any());
    }
}
