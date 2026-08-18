package com.lion._iozoo.document.presentation.api;

import com.lion._iozoo.document.application.command.CreateDocumentCommand;
import com.lion._iozoo.document.application.command.CreateDocumentRelationCommand;
import com.lion._iozoo.document.application.command.RaciAssignmentCommand;
import com.lion._iozoo.document.application.command.RequestTranslationCommand;
import com.lion._iozoo.document.application.command.RequestWritingSuggestionsCommand;
import com.lion._iozoo.document.application.command.SetDocumentRaciCommand;
import com.lion._iozoo.document.application.command.UpdateDocumentCommand;
import com.lion._iozoo.document.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.document.application.port.out.UserSummary;
import com.lion._iozoo.document.application.result.DocumentImpactResult;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;
import com.lion._iozoo.document.application.result.RequestTranslationResult;
import com.lion._iozoo.document.application.result.WritingSuggestionResult;
import com.lion._iozoo.document.application.usecase.*;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.domain.Translation;
import com.lion._iozoo.document.presentation.api.common.DocumentResponseCode;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRelationRequest;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRequest;
import com.lion._iozoo.document.presentation.api.request.RequestTranslationRequest;
import com.lion._iozoo.document.presentation.api.request.RequestWritingSuggestionsRequest;
import com.lion._iozoo.document.presentation.api.request.SetDocumentRaciRequest;
import com.lion._iozoo.document.presentation.api.request.UpdateDocumentRequest;
import com.lion._iozoo.document.presentation.api.response.DocumentImpactResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRaciResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRelationExploreResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentRelationResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentResponse;
import com.lion._iozoo.document.presentation.api.response.DocumentVersionResponse;
import com.lion._iozoo.document.presentation.api.response.MyDocumentPermissionResponse;
import com.lion._iozoo.document.presentation.api.response.TranslationResponse;
import com.lion._iozoo.document.presentation.api.response.WritingSuggestionsResponse;
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
import java.util.Map;

@Tag(name = "Document", description = "문서 CRUD API")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;
    private final UpdateDocumentUseCase updateDocumentUseCase;
    private final DeleteDocumentUseCase deleteDocumentUseCase;
    private final GetDocumentUseCase getDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;
    private final SearchDocumentsUseCase searchDocumentsUseCase;
    private final SetDocumentRaciUseCase setDocumentRaciUseCase;
    private final CreateDocumentRelationUseCase createDocumentRelationUseCase;
    private final GetDocumentRelationsUseCase getDocumentRelationsUseCase;
    private final AnalyzeDocumentImpactUseCase analyzeDocumentImpactUseCase;
    private final GetDocumentVersionsUseCase getDocumentVersionsUseCase;
    private final GetMyDocumentPermissionUseCase getMyDocumentPermissionUseCase;
    private final RequestTranslationUseCase requestTranslationUseCase;
    private final GetTranslationUseCase getTranslationUseCase;
    private final RequestWritingSuggestionsUseCase requestWritingSuggestionsUseCase;
    private final LoadUserSummaryPort loadUserSummaryPort;

    @Operation(summary = "문서 생성", description = "팀 공간에 DRAFT 상태의 새 문서를 생성한다.")
    @PostMapping
    public GlobalApiResponse<DocumentResponse> createDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid CreateDocumentRequest request) {

        CreateDocumentCommand command = new CreateDocumentCommand(
                request.teamId(),
                request.title(),
                request.blocks()
        );

        Document document = createDocumentUseCase.create(authUser.userId(), command);

        return GlobalApiResponse.created(DocumentResponseCode.DOCUMENT_CREATED,
                DocumentResponse.from(document, loadAuthorSummary(document)));
    }

    @Operation(summary = "문서 편집", description = "작성자(R) 본인이 DRAFT 상태의 문서 제목/내용을 수정한다.")
    @PatchMapping("/{documentId}")
    public GlobalApiResponse<DocumentResponse> updateDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid UpdateDocumentRequest request) {

        UpdateDocumentCommand command = new UpdateDocumentCommand(
                request.title(),
                request.blocks()
        );

        Document document = updateDocumentUseCase.update(authUser.userId(), documentId, command);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_UPDATED,
                DocumentResponse.from(document, loadAuthorSummary(document)));
    }

    @Operation(summary = "문서 상세조회", description = "문서 하나를 단건으로 조회한다 (블록 본문 포함). restricted 문서는 작성자·RACI(R/A/C)에게 노출되고, I는 OFFICIAL 문서에 한해 노출된다.")
    @GetMapping("/{documentId}")
    public GlobalApiResponse<DocumentResponse> getDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        Document document = getDocumentUseCase.getById(authUser.userId(), documentId);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_FETCHED,
                DocumentResponse.from(document, loadAuthorSummary(document)));
    }

    @Operation(summary = "문서 삭제·보관", description = "작성자(R) 또는 팀 관리자가 문서를 삭제한다.")
    @DeleteMapping("/{documentId}")
    public GlobalApiResponse<Void> deleteDocument(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        deleteDocumentUseCase.delete(authUser.userId(), documentId);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_DELETED);
    }

    @Operation(summary = "문서 목록 조회", description = "팀 공간의 문서 목록을 페이지네이션으로 조회한다. restricted 문서는 작성자·RACI(R/A/C)에게 노출되고, I는 OFFICIAL 문서에 한해 노출된다.")
    @GetMapping
    public GlobalApiResponse<Page<DocumentResponse>> listDocuments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Document> page = listDocumentsUseCase.list(authUser.userId(), teamId, pageable);
        Map<Long, UserSummary> summaries = loadAuthorSummaries(page.getContent());
        Page<DocumentResponse> documents = page.map(document ->
                DocumentResponse.from(document, summaries.get(document.getAuthorId())));

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENTS_FETCHED, documents);
    }

    @Operation(summary = "문서 검색", description = "팀 공간의 문서를 제목/내용 키워드로 검색한다.")
    @GetMapping("/search")
    public GlobalApiResponse<Page<DocumentResponse>> searchDocuments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Long teamId,
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Document> page = searchDocumentsUseCase.search(authUser.userId(), teamId, keyword, pageable);
        Map<Long, UserSummary> summaries = loadAuthorSummaries(page.getContent());
        Page<DocumentResponse> documents = page.map(document ->
                DocumentResponse.from(document, summaries.get(document.getAuthorId())));

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

    @Operation(summary = "문서 관계 그래프 탐색", description = "문서를 노드로, 상위/하위/참조/의존 관계를 양방향으로 탐색한다. restricted 이웃 문서는 RACI 접근수준이 NONE이면 결과에서 숨긴다.")
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

    @Operation(summary = "Impact Analysis 조회", description = "문서 수정 시 영향받는 연결 문서 목록을 관계 그래프에서 다단계로 탐색해 조회한다. RACI 접근수준이 NONE인 문서는 숨기고, 그 문서를 통한 하위 탐색도 하지 않는다.")
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

    @Operation(summary = "버전별 변경 이력 조회", description = "공식 문서의 버전별 변경 내용과 연결된 Doc PR 이력을 조회한다.")
    @GetMapping("/{documentId}/versions")
    public GlobalApiResponse<List<DocumentVersionResponse>> getDocumentVersions(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        List<DocumentVersion> versions = getDocumentVersionsUseCase.getVersions(authUser.userId(), documentId);

        List<DocumentVersionResponse> response = versions.stream()
                .map(DocumentVersionResponse::from)
                .toList();

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_VERSIONS_FETCHED, response);
    }

    @Operation(summary = "내 접근 권한 조회", description = "현재 사용자의 RACI 역할과 이 문서에 대한 접근수준(FULL/OFFICIAL_ONLY/NONE)을 조회한다.")
    @GetMapping("/{documentId}/my-permissions")
    public GlobalApiResponse<MyDocumentPermissionResponse> getMyDocumentPermission(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId) {

        MyDocumentPermissionResult result = getMyDocumentPermissionUseCase.getMyPermission(authUser.userId(), documentId);

        return GlobalApiResponse.ok(DocumentResponseCode.DOCUMENT_MY_PERMISSION_FETCHED,
                MyDocumentPermissionResponse.from(result));
    }

    @Operation(summary = "개발 요소 보존 번역 요청", description = "블록 하나를 번역한다 — FE가 블록마다 이 API를 개별 호출한다 (type: code인 블록은 호출하지 않고 원문 유지). 코드블록·API명·변수명은 보존하고 나머지만 번역한다(Dev-aware Translation, AI-Fighters 프록시). 같은 문서·블록·대상 언어로 이미 번역한 적 있으면 캐시된 결과를 즉시 반환한다.")
    @PostMapping("/{documentId}/translations")
    public GlobalApiResponse<TranslationResponse> requestTranslation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid RequestTranslationRequest request) {

        RequestTranslationCommand command = new RequestTranslationCommand(
                request.blockId(), request.content(), request.sourceLanguage(), request.targetLanguage());

        RequestTranslationResult result = requestTranslationUseCase.translate(authUser.userId(), documentId, command);

        return GlobalApiResponse.ok(DocumentResponseCode.TRANSLATION_REQUESTED,
                TranslationResponse.from(result.translation(), result.cached()));
    }

    @Operation(summary = "번역 결과 원문 대조 조회", description = "저장된 번역 결과를 원문과 대조할 수 있도록 다시 조회한다.")
    @GetMapping("/{documentId}/translations/{translationId}")
    public GlobalApiResponse<TranslationResponse> getTranslation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @PathVariable Long translationId) {

        Translation translation = getTranslationUseCase.getById(authUser.userId(), documentId, translationId);

        return GlobalApiResponse.ok(DocumentResponseCode.TRANSLATION_FETCHED,
                TranslationResponse.from(translation, true));
    }

    @Operation(summary = "글쓰기 제안 요청", description = "작성 중인 문서에 구조 가이드·다음 문단·명확성 제안을 받는다(AI Writing Assistant, AI-Fighters 프록시). 저장은 하지 않으며, 제안 목록만 내려준다.")
    @PostMapping("/{documentId}/writing-assistant/suggestions")
    public GlobalApiResponse<WritingSuggestionsResponse> requestWritingSuggestions(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long documentId,
            @RequestBody @Valid RequestWritingSuggestionsRequest request) {

        RequestWritingSuggestionsCommand command = new RequestWritingSuggestionsCommand(
                request.content(), request.cursorContext());

        List<WritingSuggestionResult> results = requestWritingSuggestionsUseCase.request(
                authUser.userId(), documentId, command);

        return GlobalApiResponse.ok(DocumentResponseCode.WRITING_SUGGESTIONS_FETCHED,
                WritingSuggestionsResponse.from(results));
    }

    private UserSummary loadAuthorSummary(Document document) {
        return loadUserSummaryPort.loadSummariesByUserIds(List.of(document.getAuthorId()))
                .get(document.getAuthorId());
    }

    private Map<Long, UserSummary> loadAuthorSummaries(List<Document> documents) {
        List<Long> authorIds = documents.stream().map(Document::getAuthorId).distinct().toList();
        return loadUserSummaryPort.loadSummariesByUserIds(authorIds);
    }
}
