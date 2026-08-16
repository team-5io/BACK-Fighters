package com.lion._iozoo.document.application.service;

import com.lion._iozoo.document.application.port.out.DeleteDocumentPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.usecase.DeleteDocumentUseCase;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDocumentService implements DeleteDocumentUseCase {

    private final LoadDocumentPort loadDocumentPort;
    private final DeleteDocumentPort deleteDocumentPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional
    public void delete(Long userId, Long documentId) {
        log.info("event=document_delete_시작 userId={}, documentId={}", userId, documentId);

        try {
            Document document = loadDocumentPort.loadById(documentId)
                    .orElseThrow(() -> new DocumentNotFoundException(documentId));

            teamPermissionChecker.requireMember(document.getTeamId(), userId);

            // 문서 삭제·보관은 작성자(R) 또는 팀 관리자만 가능 (Doc PR API 명세서 "문서 삭제·보관" 사용 계층 기준)
            if (!document.getAuthorId().equals(userId)) {
                teamPermissionChecker.requireAdmin(document.getTeamId(), userId);
            }

            deleteDocumentPort.deleteById(documentId);

            log.info("event=document_delete_완료 userId={}, documentId={}", userId, documentId);
        } catch (RuntimeException e) {
            log.warn("event=document_delete_실패 userId={}, documentId={}, reason={}",
                    userId, documentId, e.getMessage(), e);
            throw e;
        }
    }
}
