package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.command.RequestWritingSuggestionsCommand;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.RequestWritingSuggestionsPort;
import com.lion._iozoo.document.application.result.WritingSuggestionResult;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestWritingSuggestionsServiceTest {

    @Mock
    private LoadDocumentPort loadDocumentPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;
    @Mock
    private RequestWritingSuggestionsPort requestWritingSuggestionsPort;

    private RequestWritingSuggestionsService sut() {
        return new RequestWritingSuggestionsService(loadDocumentPort, teamPermissionChecker, requestWritingSuggestionsPort);
    }

    private Document document() {
        return Document.builder()
                .id(100L).teamId(1L).authorId(10L)
                .title("제목").content("내용")
                .status(DocumentStatus.DRAFT).restricted(false)
                .build();
    }

    private RequestWritingSuggestionsCommand command() {
        return new RequestWritingSuggestionsCommand("원문", "커서 주변 맥락");
    }

    @Test
    void 팀원이면_AI_Gateway를_호출하고_제안_목록을_반환한다() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        when(requestWritingSuggestionsPort.requestSuggestions(100L, "원문", "커서 주변 맥락"))
                .thenReturn(List.of(
                        new WritingSuggestionResult("structure", "목차를 추가하세요"),
                        new WritingSuggestionResult("clarity", "이 문장을 더 명확하게")));

        List<WritingSuggestionResult> result = sut().request(30L, 100L, command());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo("structure");
        assertThat(result.get(1).text()).isEqualTo("이 문장을 더 명확하게");
    }

    @Test
    void 문서가_없으면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().request(30L, 100L, command()))
                .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    void 팀원이_아니면_예외() {
        when(loadDocumentPort.loadById(100L)).thenReturn(Optional.of(document()));
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().request(99L, 100L, command()))
                .isInstanceOf(ForbiddenException.class);
    }
}
