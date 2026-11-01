package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.UserResidentJpaEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

@Mapper(componentModel = "spring")
public interface UserResidentMapper extends EntityMapper<UserResidentEntity, UserResidentJpaEntity>{

}
