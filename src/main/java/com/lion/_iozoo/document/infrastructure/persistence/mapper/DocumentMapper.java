package com.lion._iozoo.document.infrastructure.persistence.mapper;

import com.lion._iozoo.document.domain.Document;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DocumentMapper {

    DocumentEntity toEntity(Document document);

    Document toDomain(DocumentEntity entity);
}
