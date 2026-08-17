package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.CreateDocumentRelationCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentRelationPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RelationType;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.document.domain.exception.DocumentRelationSelfReferenceException;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
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
class CreateDocumentRelationServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private SaveDocumentRelationPort saveDocumentRelationPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private CreateDocumentRelationService sut() {
        return new CreateDocumentRelationService(loadDocumentPort, saveDocumentRelationPort, teamPermissionChecker);
    }

    private Document document(Long id) {
        return Document.builder()
                .id(id).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    @Test
    void 문서_관계를_생성한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.of(document(200L)));
        when(saveDocumentRelationPort.save(any())).thenAnswer(invocation -> {
            DocumentRelation arg = invocation.getArgument(0);
            return DocumentRelation.builder()
                    .id(1L)
                    .sourceDocumentId(arg.getSourceDocumentId())
                    .targetDocumentId(arg.getTargetDocumentId())
                    .relationType(arg.getRelationType())
                    .createdAt(arg.getCreatedAt())
                    .build();
        });

        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(200L, RelationType.REFERENCE);

        DocumentRelation result = sut().create(1L, 100L, command);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSourceDocumentId()).isEqualTo(100L);
        assertThat(result.getTargetDocumentId()).isEqualTo(200L);
        assertThat(result.getRelationType()).isEqualTo(RelationType.REFERENCE);
        verify(teamPermissionChecker).requireMember(1L, 1L);
    }

    @Test
    void source_문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(200L, RelationType.REFERENCE);

        assertThatThrownBy(() -> sut().create(1L, 100L, command))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(saveDocumentRelationPort, never()).save(any());
    }

    @Test
    void target_문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L)));
        when(loadDocumentPort.loadById(200L)).thenReturn(Optional.empty());

        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(200L, RelationType.REFERENCE);

        assertThatThrownBy(() -> sut().create(1L, 100L, command))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(saveDocumentRelationPort, never()).save(any());
    }

    @Test
    void 자기_자신과의_관계는_예외() {
        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(100L, RelationType.REFERENCE);

        assertThatThrownBy(() -> sut().create(1L, 100L, command))
                .isInstanceOf(DocumentRelationSelfReferenceException.class);

        verify(saveDocumentRelationPort, never()).save(any());
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document(100L)));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(200L, RelationType.REFERENCE);

        assertThatThrownBy(() -> sut().create(99L, 100L, command))
                .isInstanceOf(ForbiddenException.class);

        verify(saveDocumentRelationPort, never()).save(any());
    }
}
