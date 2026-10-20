package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.IdTypeJpaEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity.IdTypeEntity;

@Mapper(componentModel = "spring")
public interface IdTypeMapper extends EntityMapper<IdTypeEntity, IdTypeJpaEntity>{

}
