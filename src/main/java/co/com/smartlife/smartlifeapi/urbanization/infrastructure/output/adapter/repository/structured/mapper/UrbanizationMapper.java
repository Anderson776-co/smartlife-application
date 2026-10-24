package co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationJpaEntity;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

@Mapper(componentModel = "spring")
public interface UrbanizationMapper extends EntityMapper<UrbanizationEntity, UrbanizationJpaEntity> {

}
