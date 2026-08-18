package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.result.NextAssigneeInfoResult;

public interface GetNextAssigneeInfoUseCase {
    NextAssigneeInfoResult getInfo(Long userId, Long docPrId);
}
