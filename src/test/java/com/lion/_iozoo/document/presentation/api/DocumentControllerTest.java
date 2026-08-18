package com.lion._iozoo.document.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion._iozoo.document.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.document.application.port.out.UserSummary;
import com.lion._iozoo.document.application.result.DocumentImpactResult;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.result.DocumentRelationExploreResult;
import com.lion._iozoo.document.application.result.MyDocumentPermissionResult;
import com.lion._iozoo.document.application.usecase.*;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentAccessLevel;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.domain.RaciRole;
import com.lion._iozoo.document.domain.RelationDirection;
import com.lion._iozoo.document.domain.RelationType;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRelationRequest;
import com.lion._iozoo.document.presentation.api.request.CreateDocumentRequest;
import com.lion._iozoo.document.presentation.api.request.RaciAssignmentRequest;
import com.lion._iozoo.document.presentation.api.request.SetDocumentRaciRequest;
import com.lion._iozoo.document.presentation.api.request.UpdateDocumentRequest;
import com.lion._iozoo.global.security.AuthUser;
import com.lion._iozoo.global.security.JwtAuthenticationFilter;
import com.lion._iozoo.global.security.JwtBlacklist;
import com.lion._iozoo.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateDocumentUseCase createDocumentUseCase;

    @MockBean
    private UpdateDocumentUseCase updateDocumentUseCase;

    @MockBean
    private DeleteDocumentUseCase deleteDocumentUseCase;

    @MockBean
    private ListDocumentsUseCase listDocumentsUseCase;

    @MockBean
    private SearchDocumentsUseCase searchDocumentsUseCase;

    @MockBean
    private SetDocumentRaciUseCase setDocumentRaciUseCase;

    @MockBean
    private CreateDocumentRelationUseCase createDocumentRelationUseCase;

    @MockBean
    private GetDocumentRelationsUseCase getDocumentRelationsUseCase;

    @MockBean
    private AnalyzeDocumentImpactUseCase analyzeDocumentImpactUseCase;

    @MockBean
    private GetDocumentVersionsUseCase getDocumentVersionsUseCase;

    @MockBean
    private GetMyDocumentPermissionUseCase getMyDocumentPermissionUseCase;

    @MockBean
    private RequestTranslationUseCase requestTranslationUseCase;

    @MockBean
    private GetTranslationUseCase getTranslationUseCase;

    @MockBean
    private GetDocumentUseCase getDocumentUseCase;

    @MockBean
    private LoadUserSummaryPort loadUserSummaryPort;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtBlacklist jwtBlacklist;

    private static final Long USER_ID = 1L;

    private UsernamePasswordAuthenticationToken authToken() {
        return new UsernamePasswordAuthenticationToken(
                new AuthUser(USER_ID), null, List.of());
    }

    private Document sampleDocument() {
        return Document.builder()
                .id(100L)
                .teamId(1L)
                .authorId(USER_ID)
                .title("테스트 문서")
                .content("테스트 내용")
                .status(DocumentStatus.DRAFT)
                .restricted(false)
                .build();
    }

    @Test
    @DisplayName("POST /documents - 문서 생성 성공")
    void createDocument_success() throws Exception {
        Document document = sampleDocument();
        given(createDocumentUseCase.create(eq(USER_ID), any())).willReturn(document);
        given(loadUserSummaryPort.loadSummariesByUserIds(List.of(USER_ID)))
                .willReturn(java.util.Map.of(USER_ID, new UserSummary("김재원", "author@b.com")));

        CreateDocumentRequest request = new CreateDocumentRequest(1L, "테스트 문서",
                List.of(Block.builder().id("b1").type("paragraph").content("테스트 내용").build()));

        mockMvc.perform(post("/documents")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_201_1"))
                .andExpect(jsonPath("$.data.id").value(100L))
                .andExpect(jsonPath("$.data.title").value("테스트 문서"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.assignee.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.assignee.name").value("김재원"))
                .andExpect(jsonPath("$.data.assignee.role").value("R"));
    }

    @Test
    @DisplayName("GET /documents/{documentId} - 문서 상세조회 성공")
    void getDocument_success() throws Exception {
        Document document = sampleDocument();
        given(getDocumentUseCase.getById(eq(USER_ID), eq(100L))).willReturn(document);
        given(loadUserSummaryPort.loadSummariesByUserIds(List.of(USER_ID)))
                .willReturn(java.util.Map.of(USER_ID, new UserSummary("김재원", "author@b.com")));

        mockMvc.perform(get("/documents/100")
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_12"))
                .andExpect(jsonPath("$.data.id").value(100L))
                .andExpect(jsonPath("$.data.title").value("테스트 문서"))
                .andExpect(jsonPath("$.data.assignee.name").value("김재원"))
                .andExpect(jsonPath("$.data.assignee.role").value("R"));
    }

    @Test
    @DisplayName("PATCH /documents/{documentId} - 문서 편집 성공")
    void updateDocument_success() throws Exception {
        Document updatedDoc = Document.builder()
                .id(100L)
                .teamId(1L)
                .authorId(USER_ID)
                .title("수정된 제목")
                .content("수정된 내용")
                .status(DocumentStatus.DRAFT)
                .restricted(false)
                .build();
        given(updateDocumentUseCase.update(eq(USER_ID), eq(100L), any())).willReturn(updatedDoc);

        UpdateDocumentRequest request = new UpdateDocumentRequest("수정된 제목",
                List.of(Block.builder().id("b1").type("paragraph").content("수정된 내용").build()));

        mockMvc.perform(patch("/documents/100")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_1"))
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("PATCH /documents/{documentId} - blocks 누락 시 400 (NPE 방지)")
    void updateDocument_blocksNull_returns400() throws Exception {
        String requestJson = "{\"title\":\"수정된 제목\"}";

        mockMvc.perform(patch("/documents/100")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400_1"));
    }

    @Test
    @DisplayName("DELETE /documents/{documentId} - 문서 삭제 성공")
    void deleteDocument_success() throws Exception {
        willDoNothing().given(deleteDocumentUseCase).delete(USER_ID, 100L);

        mockMvc.perform(delete("/documents/100")
                        .with(authentication(authToken()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_2"))
                .andExpect(jsonPath("$.message").value("문서가 삭제되었습니다."));
    }

    @Test
    @DisplayName("GET /documents - 문서 목록 조회 성공")
    void listDocuments_success() throws Exception {
        Document doc = sampleDocument();
        Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 20), 1);
        given(listDocumentsUseCase.list(eq(USER_ID), eq(1L), any())).willReturn(page);

        mockMvc.perform(get("/documents")
                        .with(authentication(authToken()))
                        .param("teamId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_3"))
                .andExpect(jsonPath("$.data.content[0].id").value(100L))
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 문서"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /documents/search - 문서 검색 성공")
    void searchDocuments_success() throws Exception {
        Document doc = sampleDocument();
        Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 20), 1);
        given(searchDocumentsUseCase.search(eq(USER_ID), eq(1L), eq("테스트"), any())).willReturn(page);

        mockMvc.perform(get("/documents/search")
                        .with(authentication(authToken()))
                        .param("teamId", "1")
                        .param("keyword", "테스트")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_4"))
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 문서"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("PUT /documents/{documentId}/raci - RACI 지정 성공")
    void setDocumentRaci_success() throws Exception {
        given(setDocumentRaciUseCase.setRaci(eq(USER_ID), any())).willReturn(List.of(
                new DocumentRaciEntry(10L, RaciRole.R, USER_ID, java.time.LocalDateTime.now())
        ));

        SetDocumentRaciRequest request = new SetDocumentRaciRequest(
                List.of(new RaciAssignmentRequest(10L, RaciRole.R))
        );

        mockMvc.perform(put("/documents/100/raci")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_5"))
                .andExpect(jsonPath("$.data[0].userId").value(10L))
                .andExpect(jsonPath("$.data[0].role").value("R"));
    }

    @Test
    @DisplayName("POST /documents/{documentId}/relations - 문서 관계 생성 성공")
    void createDocumentRelation_success() throws Exception {
        DocumentRelation relation = DocumentRelation.builder()
                .id(1L)
                .sourceDocumentId(100L)
                .targetDocumentId(200L)
                .relationType(RelationType.REFERENCE)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        given(createDocumentRelationUseCase.create(eq(USER_ID), eq(100L), any())).willReturn(relation);

        CreateDocumentRelationRequest request = new CreateDocumentRelationRequest(200L, RelationType.REFERENCE);

        mockMvc.perform(post("/documents/100/relations")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_201_2"))
                .andExpect(jsonPath("$.data.sourceDocumentId").value(100L))
                .andExpect(jsonPath("$.data.targetDocumentId").value(200L))
                .andExpect(jsonPath("$.data.relationType").value("REFERENCE"));
    }

    @Test
    @DisplayName("GET /documents/{documentId}/relations - 문서 관계 그래프 탐색 성공")
    void getDocumentRelations_success() throws Exception {
        DocumentRelationExploreResult result = new DocumentRelationExploreResult(
                1L, RelationDirection.OUTGOING, RelationType.REFERENCE, 200L, "이웃 문서", java.time.LocalDateTime.now());
        given(getDocumentRelationsUseCase.explore(eq(USER_ID), eq(100L))).willReturn(List.of(result));

        mockMvc.perform(get("/documents/100/relations")
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_6"))
                .andExpect(jsonPath("$.data[0].relationId").value(1L))
                .andExpect(jsonPath("$.data[0].direction").value("OUTGOING"))
                .andExpect(jsonPath("$.data[0].neighborDocumentId").value(200L))
                .andExpect(jsonPath("$.data[0].neighborTitle").value("이웃 문서"));
    }

    @Test
    @DisplayName("GET /documents/{documentId}/impact - Impact Analysis 조회 성공")
    void getDocumentImpact_success() throws Exception {
        DocumentImpactResult result = new DocumentImpactResult(300L, "영향받는 문서", 2);
        given(analyzeDocumentImpactUseCase.analyze(eq(USER_ID), eq(100L))).willReturn(List.of(result));

        mockMvc.perform(get("/documents/100/impact")
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_7"))
                .andExpect(jsonPath("$.data[0].documentId").value(300L))
                .andExpect(jsonPath("$.data[0].title").value("영향받는 문서"))
                .andExpect(jsonPath("$.data[0].depth").value(2));
    }

    @Test
    @DisplayName("GET /documents/{documentId}/versions - 버전 이력 조회 성공")
    void getDocumentVersions_success() throws Exception {
        DocumentVersion version = DocumentVersion.builder()
                .id(1L).documentId(100L).versionNo(2).content("변경된 내용")
                .docPrId(5L).createdAt(java.time.LocalDateTime.now())
                .build();
        given(getDocumentVersionsUseCase.getVersions(eq(USER_ID), eq(100L))).willReturn(List.of(version));

        mockMvc.perform(get("/documents/100/versions")
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_8"))
                .andExpect(jsonPath("$.data[0].versionNo").value(2))
                .andExpect(jsonPath("$.data[0].content").value("변경된 내용"))
                .andExpect(jsonPath("$.data[0].docPrId").value(5L));
    }

    @Test
    @DisplayName("GET /documents/{documentId}/my-permissions - 내 접근 권한 조회 성공")
    void getMyDocumentPermission_success() throws Exception {
        MyDocumentPermissionResult result = new MyDocumentPermissionResult(
                100L, RaciRole.C, DocumentAccessLevel.FULL, false, true);
        given(getMyDocumentPermissionUseCase.getMyPermission(eq(USER_ID), eq(100L))).willReturn(result);

        mockMvc.perform(get("/documents/100/my-permissions")
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_200_9"))
                .andExpect(jsonPath("$.data.role").value("C"))
                .andExpect(jsonPath("$.data.accessLevel").value("FULL"))
                .andExpect(jsonPath("$.data.canViewDocPr").value(true));
    }
}
