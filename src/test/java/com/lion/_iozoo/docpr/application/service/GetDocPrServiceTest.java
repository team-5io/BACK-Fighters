package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDocPrServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocPrService sut() {
        return new GetDocPrService(loadDocPrPort, loadDocumentForDocPrPort, teamPermissionChecker);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    @Test
    void 팀원이면_조회한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));

        DocPr result = sut().getById(30L, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(DocPrStatus.CREATED);
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getById(30L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getById(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
}
