package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.ResubmitDocPrCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface ResubmitDocPrUseCase {
    DocPr resubmit(Long userId, ResubmitDocPrCommand command);
}
