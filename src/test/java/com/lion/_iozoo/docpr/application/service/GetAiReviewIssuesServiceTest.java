package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAiReviewIssuesServiceTest {

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

    private GetAiReviewIssuesService sut() {
        return new GetAiReviewIssuesService(loadDocPrPort, loadDocumentForDocPrPort, checkDocumentAccessPort,
                teamPermissionChecker, loadAiReviewIssuesPort);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    @Test
    void 미해결_이슈만_조회한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
        AiReviewIssue issue = AiReviewIssue.builder()
                .id(1L).docPrId(1L).severity("critical").issueType("conflict")
                .description("설명").status(AiReviewIssueStatus.UNRESOLVED).build();
        when(loadAiReviewIssuesPort.loadUnresolvedByDocPrId(1L)).thenReturn(List.of(issue));

        List<AiReviewIssue> result = sut().getUnresolvedIssues(30L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AiReviewIssueStatus.UNRESOLVED);
    }

    @Test
    void 문서_접근권한이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 40L)).thenReturn(false);

        assertThatThrownBy(() -> sut().getUnresolvedIssues(40L, 1L))
                .isInstanceOf(DocPrAccessDeniedException.class);
    }
}
