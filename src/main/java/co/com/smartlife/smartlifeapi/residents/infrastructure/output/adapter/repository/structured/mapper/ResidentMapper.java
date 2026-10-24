package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidentJpaEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentEntity;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

@Mapper(componentModel = "spring")
public interface ResidentMapper extends EntityMapper<ResidentEntity, ResidentJpaEntity>{

}
