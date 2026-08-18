package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.domain.AiReview;

public interface SaveAiReviewPort {
    // doc_pr_id는 UNIQUE라 같은 Doc PR로 재요청하면 기존 행을 갱신한다(재제출 후 재검토 등으로 최신 결과만 유지).
    AiReview saveOrReplace(AiReview aiReview);
}
