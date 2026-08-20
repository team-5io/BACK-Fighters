package com.lion._iozoo.docpr.domain;

import com.lion._iozoo.docpr.domain.exception.AiReviewIssueAlreadyProcessedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiReviewIssueTest {

    private AiReviewIssue issue() {
        return AiReviewIssue.builder()
                .id(1L).docPrId(10L).severity("critical").issueType("conflict")
                .description("설명").status(AiReviewIssueStatus.UNRESOLVED)
                .build();
    }

    @Test
    void 미해결_이슈는_해결로_전환할_수_있다() {
        AiReviewIssue issue = issue();

        issue.resolve();

        assertThat(issue.getStatus()).isEqualTo(AiReviewIssueStatus.RESOLVED);
    }

    @Test
    void 미해결_이슈는_건너뛰기로_전환할_수_있다() {
        AiReviewIssue issue = issue();

        issue.skip();

        assertThat(issue.getStatus()).isEqualTo(AiReviewIssueStatus.SKIPPED);
    }

    @Test
    void 이미_해결된_이슈는_다시_해결처리할_수_없다() {
        AiReviewIssue issue = issue();
        issue.resolve();

        assertThatThrownBy(issue::resolve).isInstanceOf(AiReviewIssueAlreadyProcessedException.class);
    }

    @Test
    void 이미_건너뛴_이슈는_해결처리할_수_없다() {
        AiReviewIssue issue = issue();
        issue.skip();

        assertThatThrownBy(issue::resolve).isInstanceOf(AiReviewIssueAlreadyProcessedException.class);
    }
}
