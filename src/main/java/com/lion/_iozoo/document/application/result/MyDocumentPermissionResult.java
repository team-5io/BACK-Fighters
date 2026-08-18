package com.lion._iozoo.document.application.result;

import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.RaciRole;

public record MyDocumentPermissionResult(
        Long documentId,
        RaciRole role,
        DocumentAccessLevel accessLevel,
        boolean isAuthor,
        boolean canViewDocPr
) {
}
