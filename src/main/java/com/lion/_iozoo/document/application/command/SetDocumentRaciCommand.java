package com.lion._iozoo.document.application.command;

import java.util.List;

public record SetDocumentRaciCommand(
        Long documentId,
        List<RaciAssignmentCommand> assignments
) {
}
