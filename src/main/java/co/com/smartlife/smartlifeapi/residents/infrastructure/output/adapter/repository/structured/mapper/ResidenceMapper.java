package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidenceJpaEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidenceEntity;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

@Mapper(componentModel = "spring")
public interface ResidenceMapper extends EntityMapper<ResidenceEntity, ResidenceJpaEntity>{
	
}
