package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.IdTypeJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;

@Mapper(componentModel = "spring")
public interface IdTypeMapper extends EntityMapper<IdTypeEntity, IdTypeJpaEntity>{

}
