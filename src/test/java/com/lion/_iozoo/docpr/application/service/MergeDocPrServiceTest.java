package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.MergeDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
import com.lion._iozoo.docpr.application.port.out.RecordDocumentVersionPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApprovedException;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergeDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private SaveDocPrPort saveDocPrPort;
    @Mock
    private SaveDocPrStatusHistoryPort saveDocPrStatusHistoryPort;
    @Mock
    private MarkDocumentOfficialPort markDocumentOfficialPort;
    @Mock
    private RecordDocumentVersionPort recordDocumentVersionPort;

    private MergeDocPrService sut() {
        return new MergeDocPrService(loadDocPrPort, saveDocPrPort, saveDocPrStatusHistoryPort,
                markDocumentOfficialPort, recordDocumentVersionPort);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void 승인권자가_APPROVED_상태를_병합확정한다() {
        DocPr docPr = docPr(DocPrStatus.APPROVED);
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr));
        when(saveDocPrPort.save(docPr)).thenReturn(docPr);

        DocPr result = sut().merge(20L, new MergeDocPrCommand(1L));

        assertThat(result.getStatus()).isEqualTo(DocPrStatus.MERGED);
        assertThat(result.getMergedAt()).isNotNull();
        verify(markDocumentOfficialPort).markOfficial(100L, "제안 내용");
        verify(recordDocumentVersionPort).record(100L, "제안 내용", 1L);
        verify(saveDocPrStatusHistoryPort).save(eq(1L), eq(DocPrStatus.APPROVED), eq(DocPrStatus.MERGED), eq(20L), isNull());
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().merge(20L, new MergeDocPrCommand(1L)))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any(), any());
        verify(recordDocumentVersionPort, never()).record(any(), any(), any());
    }

    @Test
    void 승인권자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.APPROVED)));

        assertThatThrownBy(() -> sut().merge(99L, new MergeDocPrCommand(1L)))
                .isInstanceOf(DocPrNotApproverException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any(), any());
        verify(recordDocumentVersionPort, never()).record(any(), any(), any());
    }

    @Test
    void APPROVED_상태가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));

        assertThatThrownBy(() -> sut().merge(20L, new MergeDocPrCommand(1L)))
                .isInstanceOf(DocPrNotApprovedException.class);

        verify(markDocumentOfficialPort, never()).markOfficial(any(), any());
        verify(recordDocumentVersionPort, never()).record(any(), any(), any());
    }
}
