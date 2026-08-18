package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadAiReviewPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.AiReviewNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
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
class GetAiReviewServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private CheckDocumentAccessPort checkDocumentAccessPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private LoadAiReviewPort loadAiReviewPort;

    private GetAiReviewService sut() {
        return new GetAiReviewService(
                loadDocPrPort, loadDocumentForDocPrPort, checkDocumentAccessPort, teamPermissionChecker, loadAiReviewPort);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    @Test
    void 저장된_AI_리뷰_결과를_조회한다() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
        AiReview review = AiReview.builder()
                .id(1L).docPrId(1L).hasConflict(false).isConsistent(true).violatesCharter(false)
                .evidence(null).reviewedAt(LocalDateTime.now())
                .build();
        when(loadAiReviewPort.loadByDocPrId(1L)).thenReturn(Optional.of(review));

        AiReview result = sut().getByDocPrId(30L, 1L);

        assertThat(result.getDocPrId()).isEqualTo(1L);
        assertThat(result.isConsistent()).isTrue();
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getByDocPrId(30L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().getByDocPrId(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 문서_접근권한이_FULL이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 40L)).thenReturn(false);

        assertThatThrownBy(() -> sut().getByDocPrId(40L, 1L))
                .isInstanceOf(DocPrAccessDeniedException.class);
    }

    @Test
    void 저장된_리뷰가_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
        when(loadAiReviewPort.loadByDocPrId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().getByDocPrId(30L, 1L))
                .isInstanceOf(AiReviewNotFoundException.class);
    }
}
