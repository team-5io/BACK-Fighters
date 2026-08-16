package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.RejectDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface RejectDocPrUseCase {
    DocPr reject(Long userId, RejectDocPrCommand command);
}
