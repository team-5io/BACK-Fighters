package com.lion._iozoo.document.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion._iozoo.docpr.application.port.out.MarkDocumentOfficialPort;
import com.lion._iozoo.document.application.port.out.LoadDocumentPort;
import com.lion._iozoo.document.application.port.out.SaveDocumentPort;
import com.lion._iozoo.document.domain.Block;
import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.domain.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentMergeAdapterForDocPr implements MarkDocumentOfficialPort {

    private final LoadDocumentPort loadDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final ObjectMapper objectMapper;

    /**
     * Consumer: docpr
     * Purpose: Doc PR Merge 확정 시 제안된 content(블록 배열의 JSON 문자열)를 문서에 반영하고 OFFICIAL로 승격
     */
    @Override
    @Transactional
    public void markOfficial(Long documentId, String content) {
        Document document = loadDocumentPort.loadById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        document.markOfficial(parseBlocks(content));
        saveDocumentPort.save(document);
    }

    private List<Block> parseBlocks(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<List<Block>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Doc PR 제안 내용(블록 JSON) 파싱에 실패했습니다.", e);
        }
    }
}
