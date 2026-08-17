package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.RejectDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAlreadyTerminalException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApproverException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RejectDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private SaveDocPrPort saveDocPrPort;
    @Mock
    private SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;

    private RejectDocPrService sut() {
        return new RejectDocPrService(loadDocPrPort, saveDocPrPort, saveDocPrStatusHistoryPort);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void 승인권자가_반려한다() {
        DocPr docPr = docPr(DocPrStatus.CREATED);
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr));
        when(saveDocPrPort.save(docPr)).thenReturn(docPr);

        DocPr result = sut().reject(20L, new RejectDocPrCommand(1L, "사유"));

        assertThat(result.getStatus()).isEqualTo(DocPrStatus.REJECTED);
        verify(saveDocPrStatusHistoryPort).save(eq(1L), eq(DocPrStatus.CREATED), eq(DocPrStatus.REJECTED), eq(20L), eq("사유"));
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().reject(20L, new RejectDocPrCommand(1L, "사유")))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 승인권자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));

        assertThatThrownBy(() -> sut().reject(99L, new RejectDocPrCommand(1L, "사유")))
                .isInstanceOf(DocPrNotApproverException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 이미_반려된_경우_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.REJECTED)));

        assertThatThrownBy(() -> sut().reject(20L, new RejectDocPrCommand(1L, "사유")))
                .isInstanceOf(DocPrAlreadyTerminalException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 이미_머지된_경우_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.MERGED)));

        assertThatThrownBy(() -> sut().reject(20L, new RejectDocPrCommand(1L, "사유")))
                .isInstanceOf(DocPrAlreadyTerminalException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 이미_승인된_경우_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.APPROVED)));

        assertThatThrownBy(() -> sut().reject(20L, new RejectDocPrCommand(1L, "사유")))
                .isInstanceOf(DocPrAlreadyTerminalException.class);

        verify(saveDocPrPort, never()).save(any());
    }
}
