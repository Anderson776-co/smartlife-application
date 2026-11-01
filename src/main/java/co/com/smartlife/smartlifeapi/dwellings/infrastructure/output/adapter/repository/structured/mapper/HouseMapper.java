package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.HouseJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.HouseEntity;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

@Mapper(componentModel = "spring")
public interface HouseMapper extends EntityMapper<HouseEntity, HouseJpaEntity>{

}
