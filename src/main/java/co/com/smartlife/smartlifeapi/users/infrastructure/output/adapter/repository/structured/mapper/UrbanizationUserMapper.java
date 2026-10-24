package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationUserJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UrbanizationUserEntity;

@Mapper(componentModel = "spring")
public interface UrbanizationUserMapper extends EntityMapper<UrbanizationUserEntity, UrbanizationUserJpaEntity>{

}
