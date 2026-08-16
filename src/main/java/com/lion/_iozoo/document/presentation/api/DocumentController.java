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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;
    private final UpdateDocumentUseCase updateDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final SearchDocumentsUseCase searchDocumentsUseCase;

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

    @DeleteMapping("/{documentId}")
    public GlobalApiResponse<Void> deleteDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        deleteDocumentUseCase.delete(authUser.userId(), documentId);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_DELETED);
    }

    @GetMapping
    public GlobalApiResponse<Page<DocumentResponse>> listDocuments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DocumentResponse> documents = listDocumentsUseCase.list(authUser.userId(), teamId, pageable)
                .map(DocumentResponse::from);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENTS_FETCHED, documents);
    }

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
