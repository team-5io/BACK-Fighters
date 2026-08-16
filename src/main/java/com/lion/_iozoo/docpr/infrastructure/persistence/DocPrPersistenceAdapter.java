package com.lion._iozoo.docpr.infrastructure.persistence;

import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.port.out.SaveDocPrPort;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.docpr.infrastructure.persistence.entity.DocPrEntity;
import com.lion._iozoo.docpr.infrastructure.persistence.mapper.DocPrMapper;
import com.lion._iozoo.docpr.infrastructure.persistence.repository.DocPrJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocPrPersistenceAdapter implements SaveDocPrPort, LoadDocPrPort {

    private final DocPrJpaRepository docPrJpaRepository;
    private final DocPrMapper docPrMapper;

    @Override
    public DocPr save(DocPr docPr) {
        DocPrEntity entity = docPrMapper.toEntity(docPr);
        DocPrEntity savedEntity = docPrJpaRepository.save(entity);
        return docPrMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<DocPr> loadById(Long docPrId) {
        return docPrJpaRepository.findById(docPrId)
                .map(docPrMapper::toDomain);
    }
}
