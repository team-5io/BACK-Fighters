package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ExceptionMergeDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionMergeDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private SaveDocPrPort saveDocPrPort;
    @Mock
    private SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;
    @Mock
    private MarkDocumentOfficialPort markDocumentOfficialPort;

    private ExceptionMergeDocPrService sut() {
        return new ExceptionMergeDocPrService(loadDocPrPort, saveDocPrPort, saveDocPrStatusHistoryPort, markDocumentOfficialPort);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void 승인권자가_APPROVED_아니어도_예외_병합한다() {
        DocPr docPr = docPr(DocPrStatus.HUMAN_REVIEW);
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr));
        when(saveDocPrPort.save(docPr)).thenReturn(docPr);

        DocPr result = sut().mergeWithException(20L, new ExceptionMergeDocPrCommand(1L, "긴급 배포"));

        assertThat(result.getStatus()).isEqualTo(DocPrStatus.MERGED);
        assertThat(result.isExceptionMerge()).isTrue();
        assertThat(result.getExceptionReason()).isEqualTo("긴급 배포");
        assertThat(result.getMergedAt()).isNotNull();
        verify(markDocumentOfficialPort).markOfficial(100L);
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().mergeWithException(20L, new ExceptionMergeDocPrCommand(1L, "긴급 배포")))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any());
    }

    @Test
    void 승인권자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.HUMAN_REVIEW)));

        assertThatThrownBy(() -> sut().mergeWithException(99L, new ExceptionMergeDocPrCommand(1L, "긴급 배포")))
                .isInstanceOf(DocPrNotApproverException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any());
    }

    @Test
    void 이미_종료된_DocPR이면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.MERGED)));

        assertThatThrownBy(() -> sut().mergeWithException(20L, new ExceptionMergeDocPrCommand(1L, "긴급 배포")))
                .isInstanceOf(DocPrAlreadyTerminalException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any());
    }
}
