package com.lion._iozoo.document.infrastructure.persistence.mapper;

import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface DocumentMapper {

    DocumentEntity toEntity(Document document);

    // blocks는 documents 테이블에 없는 별도 테이블 소스라 어댑터에서 직접 채운다.
    @Mapping(target = "blocks", ignore = true)
    Document toDomain(DocumentEntity entity);
}
