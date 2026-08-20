package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;

import java.util.List;

public interface SaveAiReviewIssuesPort {
    // AI가 이번에 감지한 이슈 중, 기존에 저장된 이슈와 동일 판정(issueType+relatedDocumentId+charterRuleId+blockId+description
    // 전부 일치)되는 것은 건너뛰고 새 이슈만 추가한다(기존 이슈는 보존/재요청 시 안 지움). 새로 추가된 이슈만 반환.
    List<AiReviewIssue> saveNewIssues(Long docPrId, List<AiReviewIssue> detectedIssues);

    AiReviewIssue updateStatus(Long issueId, AiReviewIssueStatus status);
}
