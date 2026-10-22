package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.RoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleEntity, RoleJpaEntity>{

}
