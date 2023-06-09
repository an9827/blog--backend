package com.blog.infrastructure.user.mapper;

import com.blog.domain.user.User;
import com.blog.infrastructure.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {

    UserEntityMapper MAPPER = Mappers.getMapper(UserEntityMapper.class);

    User toModel(UserEntity entity);

    @Mapping(source = "username", target = "nickname")
    UserEntity toEntity(User user);
}
