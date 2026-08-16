package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.SaveDocPrStatusHistoryPort;
import com.lion._iozoo.docpr.domain.DocPrStatus;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrStatusHistoryEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.DocPrStatusHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DocPrStatusHistoryPersistenceAdapter implements SaveDocPrStatusHistoryPort {

    private final DocPrStatusHistoryJpaRepository docPrStatusHistoryJpaRepository;

    @Override
    public void save(Long docPrId, DocPrStatus fromStatus, DocPrStatus toStatus, Long actorId, String reason) {
        docPrStatusHistoryJpaRepository.save(
                DocPrStatusHistoryEntity.builder()
                        .docPrId(docPrId)
                        .fromStatus(fromStatus == null ? null : fromStatus.name())
                        .toStatus(toStatus.name())
                        .actorId(actorId)
                        .reason(reason)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
