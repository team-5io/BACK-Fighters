package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.AddDocPrReviewCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrReviewPort;
import com.lion._iozoo.docpr.application.result.DocPrReview;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddDocPrReviewServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private SaveDocPrReviewPort saveDocPrReviewPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private AddDocPrReviewService sut() {
        return new AddDocPrReviewService(loadDocPrPort, loadDocumentForDocPrPort, saveDocPrReviewPort, teamPermissionChecker);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.HUMAN_REVIEW)
                .build();
    }

    @Test
    void 팀원이면_리뷰_의견을_등록한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        DocPrReview saved = new DocPrReview(1L, 1L, 5L, "의견입니다", LocalDateTime.now());
        when(saveDocPrReviewPort.save(1L, 5L, "의견입니다")).thenReturn(saved);

        DocPrReview result = sut().addReview(5L, new AddDocPrReviewCommand(1L, "의견입니다"));

        assertThat(result.reviewerId()).isEqualTo(5L);
        assertThat(result.comment()).isEqualTo("의견입니다");
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().addReview(5L, new AddDocPrReviewCommand(1L, "의견입니다")))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().addReview(99L, new AddDocPrReviewCommand(1L, "의견입니다")))
                .isInstanceOf(ForbiddenException.class);
    }
}
