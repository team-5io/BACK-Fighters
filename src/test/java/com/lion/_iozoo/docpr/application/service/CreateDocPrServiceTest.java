package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.CreateDocPrCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrDocumentNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotDraftException;
import com.lion._iozoo.docpr.domain.exception.DocPrRequesterNotAuthorException;
import com.lion._iozoo.docpr.domain.exception.DocPrSelfApprovalException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateDocPrServiceTest {

    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private SaveDocPrPort saveDocPrPort;

    private CreateDocPrService sut() {
        return new CreateDocPrService(loadDocumentForDocPrPort, teamPermissionChecker, saveDocPrPort);
    }

    private DocumentSummary document(Long authorId, boolean draft) {
        return new DocumentSummary(100L, 1L, authorId, draft);
    }

    private CreateDocPrCommand command(Long approverMemberId) {
        return new CreateDocPrCommand(100L, approverMemberId, "제안 내용");
    }

    @Test
    void 작성자_본인이_승인권자를_지정해_전환한다() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.of(document(10L, true)));
        when(teamPermissionChecker.resolveUserId(1L, 200L)).thenReturn(20L);
        when(saveDocPrPort.save(org.mockito.ArgumentMatchers.any(DocPr.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DocPr result = sut().create(10L, command(200L));

        assertThat(result.getDocumentId()).isEqualTo(100L);
        assertThat(result.getRequesterId()).isEqualTo(10L);
        assertThat(result.getApproverId()).isEqualTo(20L);
        assertThat(result.getStatus()).isEqualTo(DocPrStatus.CREATED);

        verify(teamPermissionChecker).resolveUserId(1L, 200L);

        ArgumentCaptor<DocPr> captor = ArgumentCaptor.forClass(DocPr.class);
        verify(saveDocPrPort).save(captor.capture());
        assertThat(captor.getValue().getProposedContent()).isEqualTo("제안 내용");
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().create(10L, command(200L)))
                .isInstanceOf(DocPrDocumentNotFoundException.class);

        verify(saveDocPrPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 작성자가_아니면_예외() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.of(document(10L, true)));

        assertThatThrownBy(() -> sut().create(99L, command(200L)))
                .isInstanceOf(DocPrRequesterNotAuthorException.class);

        verify(saveDocPrPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 초안_상태가_아니면_예외() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.of(document(10L, false)));

        assertThatThrownBy(() -> sut().create(10L, command(200L)))
                .isInstanceOf(DocPrNotDraftException.class);

        verify(saveDocPrPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 본인을_승인권자로_지정하면_예외() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.of(document(10L, true)));
        when(teamPermissionChecker.resolveUserId(1L, 200L)).thenReturn(10L);

        assertThatThrownBy(() -> sut().create(10L, command(200L)))
                .isInstanceOf(DocPrSelfApprovalException.class);

        verify(saveDocPrPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 승인권자가_팀원이_아니면_예외() {
        when(loadDocumentForDocPrPort.loadSummary(100L)).thenReturn(Optional.of(document(10L, true)));
        doThrow(new TeamMemberNotFoundException()).when(teamPermissionChecker).resolveUserId(1L, 200L);

        assertThatThrownBy(() -> sut().create(10L, command(200L)))
                .isInstanceOf(TeamMemberNotFoundException.class);

        verify(saveDocPrPort, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
