package com.lion._iozoo.document.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion._iozoo.document.application.result.DocumentRaciEntry;
import com.lion._iozoo.document.application.usecase.*;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.DocumentRelation;
import com.lion._iozoo.document.domain.DocumentStatus;
import com.lion._iozoo.document.domain.RaciRole;
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

        CreateDocumentRequest request = new CreateDocumentRequest(1L, "테스트 문서", "테스트 내용");

        mockMvc.perform(post("/documents")
                        .with(authentication(authToken()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DOCUMENT_201_1"))
                .andExpect(jsonPath("$.data.id").value(100L))
                .andExpect(jsonPath("$.data.title").value("테스트 문서"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
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

        UpdateDocumentRequest request = new UpdateDocumentRequest("수정된 제목", "수정된 내용");

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
}
