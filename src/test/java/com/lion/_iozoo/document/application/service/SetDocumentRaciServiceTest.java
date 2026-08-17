package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RaciAssignmentCommand;
import com.lion._iozoo.document.application.command.SetDocumentRaciCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.ReplaceDocumentRaciPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.DocumentRaciDuplicateUserException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetDocumentRaciServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private ReplaceDocumentRaciPort replaceDocumentRaciPort;
    @Mock
    private SaveDocumentPort saveDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private SetDocumentRaciService sut() {
        return new SetDocumentRaciService(loadDocumentPort, replaceDocumentRaciPort, saveDocumentPort, teamPermissionChecker);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    @Test
    void 팀_관리자가_RACI를_지정한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(replaceDocumentRaciPort.replaceAll(eq(100L), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L, List.of(
                new RaciAssignmentCommand(10L, RaciRole.R),
                new RaciAssignmentCommand(20L, RaciRole.A)
        ));

        List<DocumentRaciEntry> result = sut().setRaci(1L, command);

        assertThat(result).hasSize(2);
        verify(teamPermissionChecker).requireAdmin(1L, 1L);
        verify(teamPermissionChecker).requireMember(1L, 10L);
        verify(teamPermissionChecker).requireMember(1L, 20L);
        verify(saveDocumentPort).save(argThat(Document::isRestricted));
    }

    @Test
    void 배정을_전부_해제하면_restricted가_원복된다() {
        Document restricted = Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(true)
                .build();
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(restricted));
        when(replaceDocumentRaciPort.replaceAll(eq(100L), any())).thenReturn(List.of());

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L, List.of());

        sut().setRaci(1L, command);

        verify(saveDocumentPort).save(argThat(document -> !document.isRestricted()));
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L,
                List.of(new RaciAssignmentCommand(10L, RaciRole.R)));

        assertThatThrownBy(() -> sut().setRaci(1L, command))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(replaceDocumentRaciPort, never()).replaceAll(any(), any());
    }

    @Test
    void 팀_관리자가_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireAdmin(1L, 99L);

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L,
                List.of(new RaciAssignmentCommand(10L, RaciRole.R)));

        assertThatThrownBy(() -> sut().setRaci(99L, command))
                .isInstanceOf(ForbiddenException.class);

        verify(replaceDocumentRaciPort, never()).replaceAll(any(), any());
    }

    @Test
    void 같은_사용자가_중복되면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L, List.of(
                new RaciAssignmentCommand(10L, RaciRole.R),
                new RaciAssignmentCommand(10L, RaciRole.A)
        ));

        assertThatThrownBy(() -> sut().setRaci(1L, command))
                .isInstanceOf(DocumentRaciDuplicateUserException.class);

        verify(replaceDocumentRaciPort, never()).replaceAll(any(), any());
    }

    @Test
    void 배정_대상이_팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 30L);

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(100L,
                List.of(new RaciAssignmentCommand(30L, RaciRole.C)));

        assertThatThrownBy(() -> sut().setRaci(1L, command))
                .isInstanceOf(ForbiddenException.class);

        verify(replaceDocumentRaciPort, never()).replaceAll(any(), any());
    }
}
