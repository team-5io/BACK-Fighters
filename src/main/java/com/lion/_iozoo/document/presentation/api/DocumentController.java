package com.lion._iozoo.document.presentation.api;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.usecase.*;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.presentation.api.common.DocumentResponseCode;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRequest;
import com.lion._iozoo.document.presentation.api.request.UpdateDocumentRequest;
import com.lion._iozoo.document.presentation.api.response.DocumentResponse;
import com.lion._iozoo.global.presentation.GlobalApiResponse;
import com.lion._iozoo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Document", description = "문서 CRUD API")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;
    private final UpdateDocumentUseCase updateDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final SearchDocumentsUseCase searchDocumentsUseCase;

    @Operation(summary = "문서 생성", description = "팀 공간에 DRAFT 상태의 새 문서를 생성한다.")
    @PostMapping
    public GlobalApiResponse<DocumentResponse> createDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateDocumentRequest request) {

        CreateDocumentCommand command = new CreateDocumentCommand(
                request.teamId(),
                request.title(),
                request.content()
        );

        Document document = createDocumentUseCase.create(authUser.userId(), command);

        return GlobalApiResponse.created(DocumentResponseCode.DOCUMENT_CREATED, DocumentResponse.from(document));
    }

    @Operation(summary = "문서 편집", description = "작성자(R) 본인이 DRAFT 상태의 문서 제목/내용을 수정한다.")
    @PatchMapping("/{documentId}")
    public GlobalApiResponse<DocumentResponse> updateDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid UpdateDocumentRequest request) {

        UpdateDocumentCommand command = new UpdateDocumentCommand(
                request.title(),
                request.content()
        );

        Document document = updateDocumentUseCase.update(authUser.userId(), documentId, command);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_UPDATED, DocumentResponse.from(document));
    }

    @Operation(summary = "문서 삭제·보관", description = "작성자(R) 또는 팀 관리자가 문서를 삭제한다.")
    @DeleteMapping("/{documentId}")
    public GlobalApiResponse<Void> deleteDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        deleteDocumentUseCase.delete(authUser.userId(), documentId);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_DELETED);
    }

    @Operation(summary = "문서 목록 조회", description = "팀 공간의 문서 목록을 페이지네이션으로 조회한다. restricted 문서는 작성자 본인에게만 노출된다.")
    @GetMapping
    public GlobalApiResponse<Page<DocumentResponse>> listDocuments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DocumentResponse> documents = listDocumentsUseCase.list(authUser.userId(), teamId, pageable)
                .map(DocumentResponse::from);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENTS_FETCHED, documents);
    }

    @Operation(summary = "문서 검색", description = "팀 공간의 문서를 제목/내용 키워드로 검색한다.")
    @GetMapping("/search")
    public GlobalApiResponse<Page<DocumentResponse>> searchDocuments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DocumentResponse> documents = searchDocumentsUseCase.search(authUser.userId(), teamId, keyword, pageable)
                .map(DocumentResponse::from);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENTS_SEARCHED, documents);
    }
}
