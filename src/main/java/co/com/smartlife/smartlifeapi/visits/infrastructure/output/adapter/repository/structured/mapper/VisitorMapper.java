package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.VisitorEntityJpa;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

@Mapper(componentModel = "spring")
public interface VisitorMapper extends EntityMapper<VisitorEntity, VisitorEntityJpa>{

}
