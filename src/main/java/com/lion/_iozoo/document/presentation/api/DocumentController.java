package com.lion._iozoo.document.presentation.api;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.command.CreateDocumentRelationCommand;
import com.lion._iozoo.document.application.command.RaciAssignmentCommand;
import com.lion._iozoo.document.application.command.SetDocumentRaciCommand;
import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.result.DocumentImpactResult;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.application.usecase.*;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.presentation.api.common.DocumentResponseCode;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRelationRequest;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRequest;
import com.lion._iozoo.document.presentation.api.request.SetDocumentRaciRequest;
import com.lion._iozoo.document.presentation.api.request.UpdateDocumentRequest;
import com.lion._iozoo.document.presentation.api.response.DocumentImpactResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRaciResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRelationExploreResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRelationResponse;
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

import java.util.List;

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
    private final SetDocumentRaciUseCase setDocumentRaciUseCase;
    private final CreateDocumentRelationUseCase createDocumentRelationUseCase;
    private final GetDocumentRelationsUseCase getDocumentRelationsUseCase;
    private final AnalyzeDocumentImpactUseCase analyzeDocumentImpactUseCase;

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

    @Operation(summary = "RACI 역할 지정/변경", description = "팀 관리자가 문서의 RACI(R/A/C/I) 역할과 참여자를 지정·변경한다. 기존 배정을 전체 교체한다.")
    @PutMapping("/{documentId}/raci")
    public GlobalApiResponse<List<DocumentRaciResponse>> setDocumentRaci(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid SetDocumentRaciRequest request) {

        SetDocumentRaciCommand command = new SetDocumentRaciCommand(
                documentId,
                request.assignments().stream()
                        .map(a -> new RaciAssignmentCommand(a.userId(), a.role()))
                        .toList()
        );

        List<DocumentRaciEntry> entries = setDocumentRaciUseCase.setRaci(authUser.userId(), command);

        List<DocumentRaciResponse> response = entries.stream()
                .map(DocumentRaciResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_RACI_SET, response);
    }

    @Operation(summary = "문서 관계 생성", description = "문서 간 상위/하위/참조/의존 관계를 생성한다. Document Graph, Impact Analysis 기반 데이터.")
    @PostMapping("/{documentId}/relations")
    public GlobalApiResponse<DocumentRelationResponse> createDocumentRelation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid CreateDocumentRelationRequest request) {

        CreateDocumentRelationCommand command = new CreateDocumentRelationCommand(
                request.targetDocumentId(),
                request.relationType()
        );

        DocumentRelation relation = createDocumentRelationUseCase.create(authUser.userId(), documentId, command);

        return GlobalApiResponse.created(DocumentResponseCode.DOCUMENT_RELATION_CREATED,
                DocumentRelationResponse.from(relation));
    }

    @Operation(summary = "문서 관계 그래프 탐색", description = "문서를 노드로, 상위/하위/참조/의존 관계를 양방향으로 탐색한다. 지정 참여자 전용 문서는 작성자 본인이 아니면 결과에서 숨긴다.")
    @GetMapping("/{documentId}/relations")
    public GlobalApiResponse<List<DocumentRelationExploreResponse>> getDocumentRelations(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        List<DocumentRelationExploreResult> results = getDocumentRelationsUseCase.explore(authUser.userId(), documentId);

        List<DocumentRelationExploreResponse> response = results.stream()
                .map(DocumentRelationExploreResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_RELATIONS_FETCHED, response);
    }

    @Operation(summary = "Impact Analysis 조회", description = "문서 수정 시 영향받는 연결 문서 목록을 관계 그래프에서 다단계로 탐색해 조회한다. 지정 참여자 전용 문서는 작성자 본인이 아니면 숨기고, 그 문서를 통한 하위 탐색도 하지 않는다.")
    @GetMapping("/{documentId}/impact")
    public GlobalApiResponse<List<DocumentImpactResponse>> getDocumentImpact(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        List<DocumentImpactResult> results = analyzeDocumentImpactUseCase.analyze(authUser.userId(), documentId);

        List<DocumentImpactResponse> response = results.stream()
                .map(DocumentImpactResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_IMPACT_FETCHED, response);
    }
}
