package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDocPrHistoryServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private LoadDocPrStatusHistoryPort loadDocPrStatusHistoryPort;
    @Mock
    private CheckDocumentAccessPort checkDocumentAccessPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocPrHistoryService sut() {
        return new GetDocPrHistoryService(loadDocPrPort, loadDocumentForDocPrPort, loadDocPrStatusHistoryPort, checkDocumentAccessPort, teamPermissionChecker);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.MERGED)
                .build();
    }

    @Test
    void 팀원이면_이력을_조회한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
        List<DocPrHistoryEntry> entries = List.of(
                new DocPrHistoryEntry(null, DocPrStatus.CREATED, 10L, null, LocalDateTime.now()),
                new DocPrHistoryEntry(DocPrStatus.CREATED, DocPrStatus.APPROVED, 20L, null, LocalDateTime.now())
        );
        when(loadDocPrStatusHistoryPort.loadByDocPrId(1L)).thenReturn(entries);

        List<DocPrHistoryEntry> result = sut().getHistory(30L, 1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(1).toStatus()).isEqualTo(DocPrStatus.APPROVED);
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getHistory(30L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getHistory(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 문서_접근권한이_FULL이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 40L)).thenReturn(false);

        assertThatThrownBy(() -> sut().getHistory(40L, 1L))
                .isInstanceOf(DocPrAccessDeniedException.class);
    }
}
