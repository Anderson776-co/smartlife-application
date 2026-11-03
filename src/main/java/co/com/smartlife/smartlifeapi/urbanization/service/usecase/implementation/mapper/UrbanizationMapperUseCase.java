package co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationDomain;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

@Mapper(componentModel = "spring")
public interface UrbanizationMapperUseCase extends DomainMapper<UrbanizationDomain, UrbanizationEntity> {
}
