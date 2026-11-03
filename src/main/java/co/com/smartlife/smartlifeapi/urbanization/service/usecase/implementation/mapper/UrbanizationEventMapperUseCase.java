package co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationDomain;
import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;

@Mapper(componentModel = "spring")
public interface UrbanizationEventMapperUseCase {

	@Mapping(target = "urbanizationId", source = "id")
	UrbanizationUpdatedEvent toEvent(UrbanizationDomain domain);

}
