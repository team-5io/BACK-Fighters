package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.CheckDocumentAccessPort;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.RequestDocumentLionReviewPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewPort;
import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;
import com.lion._iozoo.docpr.domain.AiReview;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAccessDeniedException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAiReviewServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private CheckDocumentAccessPort checkDocumentAccessPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private RequestDocumentLionReviewPort requestDocumentLionReviewPort;
    @Mock
    private SaveAiReviewPort saveAiReviewPort;

    private RequestAiReviewService sut() {
        return new RequestAiReviewService(loadDocPrPort, loadDocumentForDocPrPort, checkDocumentAccessPort,
                teamPermissionChecker, loadUserPort, requestDocumentLionReviewPort, saveAiReviewPort);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    private User user(Long id, UUID publicId) {
        return User.builder()
                .id(id).publicId(publicId).email("a@b.com").password("hashed")
                .name("이름").timezone("Asia/Seoul").language("ko")
                .build();
    }

    @Test
    void 팀원이면_AI_리뷰를_요청하고_결과를_저장한다() {
        UUID publicId = UUID.randomUUID();
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 30L)).thenReturn(true);
        when(loadUserPort.loadUserById(30L)).thenReturn(Optional.of(user(30L, publicId)));
        when(requestDocumentLionReviewPort.requestReview(100L, 1L, 1L, publicId, "제안 내용"))
                .thenReturn(new DocumentLionGatewayResult(false, true, true, "[critical/charter_violation] 위반"));
        when(saveAiReviewPort.saveOrReplace(any())).thenAnswer(invocation -> {
            AiReview arg = invocation.getArgument(0);
            return AiReview.builder()
                    .id(1L).docPrId(arg.getDocPrId()).hasConflict(arg.isHasConflict())
                    .isConsistent(arg.isConsistent()).violatesCharter(arg.isViolatesCharter())
                    .evidence(arg.getEvidence()).reviewedAt(arg.getReviewedAt())
                    .build();
        });

        AiReview result = sut().request(30L, 1L);

        assertThat(result.getDocPrId()).isEqualTo(1L);
        assertThat(result.isViolatesCharter()).isTrue();
        assertThat(result.getEvidence()).isEqualTo("[critical/charter_violation] 위반");
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().request(30L, 1L))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(requestDocumentLionReviewPort, never()).requestReview(any(), any(), any(), any(), any());
        verify(saveAiReviewPort, never()).saveOrReplace(any());
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().request(99L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 문서_접근권한이_FULL이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr()));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(checkDocumentAccessPort.hasFullAccess(100L, 40L)).thenReturn(false);

        assertThatThrownBy(() -> sut().request(40L, 1L))
                .isInstanceOf(DocPrAccessDeniedException.class);
    }
}
