package com.lion._iozoo.docpr.application.usecase;

import com.lion._iozoo.docpr.application.command.ChangeDocPrApproverCommand;
import com.lion._iozoo.docpr.domain.DocPr;

public interface ChangeDocPrApproverUseCase {
    DocPr changeApprover(Long userId, ChangeDocPrApproverCommand command);
}
