package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.exception.AiReviewIssueNotFoundException;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.AiReviewIssueEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.AiReviewIssueJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiReviewIssuePersistenceAdapterTest {

    @Mock
    private AiReviewIssueJpaRepository aiReviewIssueJpaRepository;

    private AiReviewIssuePersistenceAdapter sut() {
        return new AiReviewIssuePersistenceAdapter(aiReviewIssueJpaRepository);
    }

    private AiReviewIssueEntity existingEntity() {
        return AiReviewIssueEntity.builder()
                .id(1L).docPrId(10L).severity("critical").issueType("conflict")
                .description("동일 이슈").relatedDocumentId(200L).status(AiReviewIssueStatus.RESOLVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AiReviewIssue detected(String description, Long relatedDocumentId) {
        return AiReviewIssue.builder()
                .docPrId(10L).severity("critical").issueType("conflict")
                .description(description).relatedDocumentId(relatedDocumentId)
                .status(AiReviewIssueStatus.UNRESOLVED).createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 기존_이슈와_동일한_이슈는_다시_저장하지_않는다() {
        when(aiReviewIssueJpaRepository.findByDocPrId(10L)).thenReturn(List.of(existingEntity()));

        List<AiReviewIssue> result = sut().saveNewIssues(10L, List.of(detected("동일 이슈", 200L)));

        assertThat(result).isEmpty();
        verify(aiReviewIssueJpaRepository).saveAll(List.of());
    }

    @Test
    void 기존_이슈와_다른_새_이슈는_추가한다() {
        when(aiReviewIssueJpaRepository.findByDocPrId(10L)).thenReturn(List.of(existingEntity()));
        AiReviewIssueEntity savedEntity = AiReviewIssueEntity.builder()
                .id(2L).docPrId(10L).severity("critical").issueType("conflict")
                .description("새로운 이슈").relatedDocumentId(300L).status(AiReviewIssueStatus.UNRESOLVED)
                .createdAt(LocalDateTime.now()).build();
        when(aiReviewIssueJpaRepository.saveAll(any())).thenReturn(List.of(savedEntity));

        List<AiReviewIssue> result = sut().saveNewIssues(10L, List.of(detected("새로운 이슈", 300L)));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("새로운 이슈");
        assertThat(result.get(0).getStatus()).isEqualTo(AiReviewIssueStatus.UNRESOLVED);

        ArgumentCaptor<List<AiReviewIssueEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(aiReviewIssueJpaRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getStatus()).isEqualTo(AiReviewIssueStatus.UNRESOLVED);
    }

    @Test
    void 같은_배치_안에서도_중복이면_한번만_추가한다() {
        when(aiReviewIssueJpaRepository.findByDocPrId(10L)).thenReturn(List.of());
        when(aiReviewIssueJpaRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        sut().saveNewIssues(10L, List.of(detected("중복", 300L), detected("중복", 300L)));

        ArgumentCaptor<List<AiReviewIssueEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(aiReviewIssueJpaRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void 상태를_변경한다() {
        AiReviewIssueEntity entity = existingEntity();
        when(aiReviewIssueJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        AiReviewIssue result = sut().updateStatus(1L, AiReviewIssueStatus.SKIPPED);

        assertThat(result.getStatus()).isEqualTo(AiReviewIssueStatus.SKIPPED);
    }

    @Test
    void 존재하지_않는_이슈_상태변경은_예외() {
        when(aiReviewIssueJpaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut().updateStatus(999L, AiReviewIssueStatus.RESOLVED))
                .isInstanceOf(AiReviewIssueNotFoundException.class);
    }
}
