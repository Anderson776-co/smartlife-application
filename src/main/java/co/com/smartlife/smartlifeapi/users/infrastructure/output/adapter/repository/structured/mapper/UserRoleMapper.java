package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserRoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserRoleMapper extends EntityMapper<UserRoleEntity, UserRoleJpaEntity>{

}
