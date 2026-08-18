package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.domain.RaciRole;

import java.util.List;

// 문서의 RACI 배정 목록에서 특정 사용자의 역할을 찾는다. 배정이 없으면 null.
final class RaciRoleLookup {

    private RaciRoleLookup() {
    }

    static RaciRole roleOf(List<DocumentRaciEntry> entries, Long userId) {
        return entries.stream()
                .filter(entry -> entry.userId().equals(userId))
                .map(DocumentRaciEntry::role)
                .findFirst()
                .orElse(null);
    }
}
