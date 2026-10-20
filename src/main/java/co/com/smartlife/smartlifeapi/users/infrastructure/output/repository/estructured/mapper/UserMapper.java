package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserEntity, UserJpaEntity>{

}
