package com.lion._iozoo.document.infrastructure.persistence.mapper;

import com.lion._iozoo.document.domain.DocumentVersion;
import com.lion._iozoo.document.infrastructure.persistence.entity.DocumentVersionEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DocumentVersionMapper {

    DocumentVersionEntity toEntity(DocumentVersion documentVersion);

    DocumentVersion toDomain(DocumentVersionEntity entity);
}
