package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.result.DocPrHistoryEntry;
import com.lion._iozoo.docpr.application.result.NextAssigneeInfoResult;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
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
class GetNextAssigneeInfoServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private LoadDocPrStatusHistoryPort loadDocPrStatusHistoryPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetNextAssigneeInfoService sut() {
        return new GetNextAssigneeInfoService(loadDocPrPort, loadDocumentForDocPrPort, loadDocPrStatusHistoryPort, teamPermissionChecker);
    }

    private DocPr docPr(DocPrStatus status, Long nextAssigneeId) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L).nextAssigneeId(nextAssigneeId)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void REVIEWER_NEEDED_상태면_needsNextAssignee가_true다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.REVIEWER_NEEDED, null)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        List<DocPrHistoryEntry> history = List.of(
                new DocPrHistoryEntry(DocPrStatus.CREATED, DocPrStatus.HUMAN_REVIEW, 10L, null, LocalDateTime.now()),
                new DocPrHistoryEntry(DocPrStatus.HUMAN_REVIEW, DocPrStatus.REVIEWER_NEEDED, 20L, "휴가", LocalDateTime.now())
        );
        when(loadDocPrStatusHistoryPort.loadByDocPrId(1L)).thenReturn(history);

        NextAssigneeInfoResult result = sut().getInfo(30L, 1L);

        assertThat(result.needsNextAssignee()).isTrue();
        assertThat(result.nextAssigneeId()).isNull();
        assertThat(result.latestHandoff().toStatus()).isEqualTo(DocPrStatus.REVIEWER_NEEDED);
        assertThat(result.latestHandoff().reason()).isEqualTo("휴가");
    }

    @Test
    void 다른_상태면_needsNextAssignee가_false고_이력이_없으면_handoff는_null이다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED, null)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(loadDocPrStatusHistoryPort.loadByDocPrId(1L)).thenReturn(List.of());

        NextAssigneeInfoResult result = sut().getInfo(30L, 1L);

        assertThat(result.needsNextAssignee()).isFalse();
        assertThat(result.latestHandoff()).isNull();
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getInfo(30L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED, null)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getInfo(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
}
