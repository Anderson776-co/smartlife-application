package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserRoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserRoleMapper extends EntityMapper<UserRoleEntity, UserRoleJpaEntity>{

}
