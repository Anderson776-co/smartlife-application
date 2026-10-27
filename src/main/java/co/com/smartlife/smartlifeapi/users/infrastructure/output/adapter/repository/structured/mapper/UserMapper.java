package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserEntity, UserJpaEntity>{

}
