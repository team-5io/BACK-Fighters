package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.global.exception.ForbiddenException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDocPrsServiceTest {

    @Mock
    private LoadDocPrPort loadDocPrPort;
    @Mock
    private TeamPermissionChecker teamPermissionChecker;

    private ListDocPrsService sut() {
        return new ListDocPrsService(loadDocPrPort, teamPermissionChecker);
    }

    private DocPr docPr() {
        return DocPr.builder()
                .id(1L).documentId(100L).requesterId(10L).approverId(20L)
                .proposedContent("제안 내용").status(DocPrStatus.CREATED)
                .build();
    }

    @Test
    void 팀원이면_목록을_조회한다() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<DocPr> page = new PageImpl<>(java.util.List.of(docPr()), pageable, 1);
        when(loadDocPrPort.loadByTeamId(eq(1L), eq(10L), any())).thenReturn(page);

        Page<DocPr> result = sut().list(10L, 1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void 팀_소속이_아니면_예외() {
        doThrow(new ForbiddenException()).when(teamPermissionChecker).requireMember(1L, 99L);

        assertThatThrownBy(() -> sut().list(99L, 1L, PageRequest.of(0, 20)))
                .isInstanceOf(ForbiddenException.class);
    }
}
