package com.lion._iozoo.document.application.usecase;

import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;

public interface GetMyDocumentPermissionUseCase {
    MyDocumentPermissionResult getMyPermission(Long userId, Long documentId);
}
