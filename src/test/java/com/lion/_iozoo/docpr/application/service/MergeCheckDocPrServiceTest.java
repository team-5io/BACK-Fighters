package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.result.MergeCheckResult;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotApproverException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergeCheckDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;

    private MergeCheckDocPrService sut() {
        return new MergeCheckDocPrService(loadDocPrPort);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void APPROVED_상태면_mergeable_true() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.APPROVED)));

        MergeCheckResult result = sut().checkMergeable(20L, 1L);

        assertThat(result.mergeable()).isTrue();
        assertThat(result.reason()).isNull();
    }

    @Test
    void APPROVED_아니면_mergeable_false() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));

        MergeCheckResult result = sut().checkMergeable(20L, 1L);

        assertThat(result.mergeable()).isFalse();
        assertThat(result.reason()).isNotBlank();
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().checkMergeable(20L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 승인권자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.APPROVED)));

        assertThatThrownBy(() -> sut().checkMergeable(99L, 1L))
                .isInstanceOf(DocPrNotApproverException.class);
    }
}
