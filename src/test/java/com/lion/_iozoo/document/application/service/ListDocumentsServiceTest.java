package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDocumentsServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private ListDocumentsService sut() {
        return new ListDocumentsService(loadDocumentPort, teamPermissionChecker);
    }

    @Test
    void 팀원_본인_userId로_restricted_필터링을_위임한다() {
        Pageable pageable = PageRequest.of(0, 20);
        Document doc = Document.builder()
                .id(1L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
        Page<Document> page = new PageImpl<>(List.of(doc), pageable, 1);
        when(loadDocumentPort.loadByTeamId(1L, 10L, pageable)).thenReturn(page);

        Page<Document> result = sut().list(10L, 1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(teamPermissionChecker).requireMember(1L, 10L);
        verify(loadDocumentPort).loadByTeamId(1L, 10L, pageable);
    }
}
