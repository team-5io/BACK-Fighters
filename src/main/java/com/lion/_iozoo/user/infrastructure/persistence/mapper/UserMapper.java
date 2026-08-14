package com.lion._iozoo.user.infrastructure.persistence.mapper;

import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.infrastructure.persistence.entity.UserEntity;
import com.lion._iozoo.global.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    // Domain -> Entity 변환
    UserEntity toEntity(User user);

    // Entity -> Domain 변환
    User toDomain(UserEntity userEntity);
}