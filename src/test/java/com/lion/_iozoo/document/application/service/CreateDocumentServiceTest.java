package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateDocumentServiceTest {

    @Mock
    private SaveDocumentPort saveDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private CreateDocumentService sut() {
        return new CreateDocumentService(saveDocumentPort, teamPermissionChecker);
    }

    @Test
    void 팀원이_초안_문서를_생성한다() {
        when(saveDocumentPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Document result = sut().create(10L, new CreateDocumentCommand(1L, "제목", "내용"));

        assertThat(result.getAuthorId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(result.isRestricted()).isFalse();
        verify(teamPermissionChecker).requireMember(1L, 10L);
    }
}
