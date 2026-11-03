package co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationDomain;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

/** Dominio hacia entidad (para el repositorio) y entidad hacia dominio (lo que el repositorio devuelve). */
@Mapper(componentModel = "spring")
public interface UrbanizationMapperUseCase extends DomainMapper<UrbanizationDomain, UrbanizationEntity> {
}
