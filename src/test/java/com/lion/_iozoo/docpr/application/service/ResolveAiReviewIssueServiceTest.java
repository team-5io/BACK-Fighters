package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewIssuesPort;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.AiReviewIssueAlreadyProcessedException;
import com.lion._iozoo.docpr.domain.exception.AiReviewIssueNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveAiReviewIssueServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private CheckDocumentAccessPort checkDocumentAccessPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private LoadAiReviewIssuesPort loadAiReviewIssuesPort;
    @Mock
    private SaveAiReviewIssuesPort saveAiReviewIssuesPort;

    private ResolveAiReviewIssueService sut() {
        return new ResolveAiReviewIssueService(loadDocPrPort, loadDocumentForDocPrPort, checkDocumentAccessPort,
                teamPermissionChecker, loadAiReviewIssuesPort, saveAiReviewIssuesPort);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    private AiReviewIssue issue(AiReviewIssueStatus status) {
        return AiReviewIssue.builder()
                .id(5L).docPrId(1L).severity("critical").issueType("conflict")
                .description("설명").status(status).build();
    }

    private void stubAccess() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
    }

    @Test
    void 미해결_이슈를_해결처리한다() {
        stubAccess();
        when(loadAiReviewIssuesPort.loadById(5L)).thenReturn(Optional.of(issue(AiReviewIssueStatus.UNRESOLVED)));
        when(saveAiReviewIssuesPort.updateStatus(5L, AiReviewIssueStatus.RESOLVED))
                .thenReturn(issue(AiReviewIssueStatus.RESOLVED));

        AiReviewIssue result = sut().resolve(30L, 1L, 5L);

        assertThat(result.getStatus()).isEqualTo(AiReviewIssueStatus.RESOLVED);
    }

    @Test
    void 이미_처리된_이슈는_예외() {
        stubAccess();
        when(loadAiReviewIssuesPort.loadById(5L)).thenReturn(Optional.of(issue(AiReviewIssueStatus.SKIPPED)));

        assertThatThrownBy(() -> sut().resolve(30L, 1L, 5L))
                .isInstanceOf(AiReviewIssueAlreadyProcessedException.class);
    }

    @Test
    void 다른_DocPR_소속_이슈면_찾을수없음_예외() {
        stubAccess();
        AiReviewIssue otherDocPrIssue = AiReviewIssue.builder()
                .id(5L).docPrId(999L).severity("critical").issueType("conflict")
                .description("설명").status(AiReviewIssueStatus.UNRESOLVED).build();
        when(loadAiReviewIssuesPort.loadById(5L)).thenReturn(Optional.of(otherDocPrIssue));

        assertThatThrownBy(() -> sut().resolve(30L, 1L, 5L))
                .isInstanceOf(AiReviewIssueNotFoundException.class);
    }
}
