package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.command.ChangeDocPrApproverCommand;
import com.lion._iozoo.docpr.application.port.out.DocumentSummary;
import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.LoadDocumentForDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.domain.exception.DocPrAlreadyTerminalException;
import com.lion._iozoo.docpr.domain.exception.DocPrNotFoundException;
import com.lion._iozoo.docpr.domain.exception.DocPrSelfApprovalException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import com.lion._iozoo.team.domain.exception.TeamMemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeDocPrApproverServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private LoadDocumentForDocPrPort loadDocumentForDocPrPort;
    @Mock
    private SaveDocPrPort saveDocPrPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private ChangeDocPrApproverService sut() {
        return new ChangeDocPrApproverService(loadDocPrPort, loadDocumentForDocPrPort, saveDocPrPort, teamPermissionChecker);
    }

    private DocPr docPr(DocPrStatus status) {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(status)
                .build();
    }

    @Test
    void 팀_관리자가_승인권자를_교체한다() {
        DocPr docPr = docPr(DocPrStatus.CREATED);
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(teamPermissionChecker.resolveUserId(1L, 300L)).thenReturn(30L);
        when(saveDocPrPort.save(docPr)).thenReturn(docPr);

        DocPr result = sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 300L));

        assertThat(result.getApproverId()).isEqualTo(30L);
        verify(teamPermissionChecker).requireAdmin(1L, 99L);
        verify(teamPermissionChecker).resolveUserId(1L, 300L);
    }

    @Test
    void DocPR이_없으면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 300L)))
                .isInstanceOf(DocPrNotFoundException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 팀_관리자가_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        assertThatThrownBy(() -> sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 300L)))
                .isInstanceOf(ForbiddenException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 이미_종료된_DocPR이면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.MERGED)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));

        assertThatThrownBy(() -> sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 300L)))
                .isInstanceOf(DocPrAlreadyTerminalException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 새_승인권자가_요청자_본인이면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        when(teamPermissionChecker.resolveUserId(1L, 400L)).thenReturn(10L);

        assertThatThrownBy(() -> sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 400L)))
                .isInstanceOf(DocPrSelfApprovalException.class);

        verify(saveDocPrPort, never()).save(any());
    }

    @Test
    void 새_승인권자가_팀원이_아니면_예외() {
        when(loadDocPrPort.loadById(1L)).thenReturn(Optional.of(docPr(DocPrStatus.CREATED)));
        when(loadDocumentForDocPrPort.loadSummary(100L))
                .thenReturn(Optional.of(new DocumentSummary(100L, 1L, 10L, true)));
        doThrow(new TeamMemberNotFoundException()).when(teamPermissionChecker).resolveUserId(1L, 300L);

        assertThatThrownBy(() -> sut().changeApprover(99L, new ChangeDocPrApproverCommand(1L, 300L)))
                .isInstanceOf(TeamMemberNotFoundException.class);

        verify(saveDocPrPort, never()).save(any());
    }
}
