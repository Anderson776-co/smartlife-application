package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.AuthorizingResidentEntityJpa;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingResidentEntity;

@Mapper(componentModel = "spring")
public interface AuthorizingResidentMapper extends EntityMapper<AuthorizingResidentEntity, AuthorizingResidentEntityJpa>{

}
