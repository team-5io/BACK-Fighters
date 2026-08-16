package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.result.MergeCheckResult;

public interface MergeCheckDocPrUseCase {
    MergeCheckResult checkMergeable(Long userId, Long docPrId);
}
