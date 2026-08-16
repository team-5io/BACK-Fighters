package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.domain.DocPr;

public interface GetDocPrUseCase {
    DocPr getById(Long userId, Long docPrId);
}
