package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.ExceptionMergeDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface ExceptionMergeDocPrUseCase {
    DocPr mergeWithException(Long userId, ExceptionMergeDocPrCommand command);
}
