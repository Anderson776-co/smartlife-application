package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.PropertyVisitEntityJpa;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;

@Mapper(componentModel = "spring")
public interface PropertyVisitMapper extends EntityMapper<PropertyVisitEntity, PropertyVisitEntityJpa>{

}
