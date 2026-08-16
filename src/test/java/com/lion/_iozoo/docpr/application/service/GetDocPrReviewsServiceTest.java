package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrReviewsPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDocPrReviewsServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private LoadDocPrReviewsPort loadDocPrReviewsPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private GetDocPrReviewsService sut() {
        return new GetDocPrReviewsService(loadDocPrPort, loadDocumentForDocPrPort, loadDocPrReviewsPort, teamPermissionChecker);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.HUMAN_REVIEW)
                .build();
    }

    @Test
    void 팀원이면_리뷰_목록을_조회한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        List<DocPrReview> reviews = List.of(
                new DocPrReview(1L, 1L, 5L, "의견1", LocalDateTime.now()),
                new DocPrReview(2L, 1L, 20L, "의견2", LocalDateTime.now())
        );
        when(loadDocPrReviewsPort.loadByDocPrId(1L)).thenReturn(reviews);

        List<DocPrReview> result = sut().getReviews(5L, 1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(1).comment()).isEqualTo("의견2");
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getReviews(5L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, false)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getReviews(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
}
