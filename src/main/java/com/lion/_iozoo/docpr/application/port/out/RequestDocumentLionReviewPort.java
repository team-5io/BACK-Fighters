package com.lion._iozoo.docpr.application.port.out;

import com.lion._iozoo.docpr.application.result.DocumentLionGatewayResult;

public interface RequestDocumentLionReviewPort {
    // 실패(연결 실패/타임아웃/비2xx) 시 AiReviewFailedException을 던진다.
    DocumentLionGatewayResult requestReview(DocumentLionReviewRequest request);
}
