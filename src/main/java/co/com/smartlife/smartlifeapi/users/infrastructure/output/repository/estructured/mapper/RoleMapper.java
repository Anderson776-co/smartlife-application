package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.RoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleEntity, RoleJpaEntity>{

}
