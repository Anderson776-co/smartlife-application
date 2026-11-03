package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceDomain;
import co.com.smartlife.smartlifeapi.residents.event.ResidentResidenceUpdatedEvent;

@Mapper(componentModel = "spring")
public interface ResidentResidenceEventMapperUseCase {

	@Mapping(target = "residentResidenceId", source = "id")
	@Mapping(target = "residentId", source = "resident.id")
	ResidentResidenceUpdatedEvent toEvent(ResidentResidenceDomain domain);

}
