package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.LoadAiReviewIssuesPort;
import com.lion._iozoo.docpr.application.port.out.SaveAiReviewIssuesPort;
import com.lion._iozoo.docpr.domain.AiReviewIssue;
import com.lion._iozoo.docpr.domain.AiReviewIssueStatus;
import com.lion._iozoo.docpr.domain.exception.AiReviewIssueNotFoundException;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.AiReviewIssueEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.AiReviewIssueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AiReviewIssuePersistenceAdapter implements LoadAiReviewIssuesPort, SaveAiReviewIssuesPort {

    private final AiReviewIssueJpaRepository aiReviewIssueJpaRepository;

    @Override
    public List<AiReviewIssue> loadByDocPrId(Long docPrId) {
        return aiReviewIssueJpaRepository.findByDocPrId(docPrId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<AiReviewIssue> loadUnresolvedByDocPrId(Long docPrId) {
        return aiReviewIssueJpaRepository.findByDocPrIdAndStatus(docPrId, AiReviewIssueStatus.UNRESOLVED)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<AiReviewIssue> loadById(Long issueId) {
        return aiReviewIssueJpaRepository.findById(issueId).map(this::toDomain);
    }

    @Override
    @Transactional
    public List<AiReviewIssue> saveNewIssues(Long docPrId, List<AiReviewIssue> detectedIssues) {
        Set<String> existingKeys = new HashSet<>();
        for (AiReviewIssueEntity entity : aiReviewIssueJpaRepository.findByDocPrId(docPrId)) {
            existingKeys.add(dedupKey(entity.getIssueType(), entity.getRelatedDocumentId(),
                    entity.getCharterRuleId(), entity.getBlockId(), entity.getDescription()));
        }

        List<AiReviewIssueEntity> toInsert = new ArrayList<>();
        for (AiReviewIssue issue : detectedIssues) {
            String key = dedupKey(issue.getIssueType(), issue.getRelatedDocumentId(),
                    issue.getCharterRuleId(), issue.getBlockId(), issue.getDescription());
            if (existingKeys.add(key)) {
                toInsert.add(AiReviewIssueEntity.builder()
                        .docPrId(docPrId)
                        .severity(issue.getSeverity())
                        .issueType(issue.getIssueType())
                        .description(issue.getDescription())
                        .relatedDocumentId(issue.getRelatedDocumentId())
                        .charterRuleId(issue.getCharterRuleId())
                        .blockId(issue.getBlockId())
                        .quote(issue.getQuote())
                        .status(AiReviewIssueStatus.UNRESOLVED)
                        .createdAt(issue.getCreatedAt())
                        .build());
            }
        }

        return aiReviewIssueJpaRepository.saveAll(toInsert).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public AiReviewIssue updateStatus(Long issueId, AiReviewIssueStatus status) {
        AiReviewIssueEntity entity = aiReviewIssueJpaRepository.findById(issueId)
                .orElseThrow(() -> new AiReviewIssueNotFoundException(issueId));
        entity.setStatus(status);
        return toDomain(entity);
    }

    private String dedupKey(String issueType, Long relatedDocumentId, String charterRuleId, String blockId, String description) {
        return String.join("|",
                Objects.toString(issueType, ""),
                Objects.toString(relatedDocumentId, ""),
                Objects.toString(charterRuleId, ""),
                Objects.toString(blockId, ""),
                Objects.toString(description, ""));
    }

    private AiReviewIssue toDomain(AiReviewIssueEntity entity) {
        return AiReviewIssue.builder()
                .id(entity.getId())
                .docPrId(entity.getDocPrId())
                .severity(entity.getSeverity())
                .issueType(entity.getIssueType())
                .description(entity.getDescription())
                .relatedDocumentId(entity.getRelatedDocumentId())
                .charterRuleId(entity.getCharterRuleId())
                .blockId(entity.getBlockId())
                .quote(entity.getQuote())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
