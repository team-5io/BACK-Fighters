package com.lion._iozoo.docpr.application.command;

public record ChangeDocPrApproverCommand(
        Long docPrId,
        Long newApproverMemberId
) {
}
