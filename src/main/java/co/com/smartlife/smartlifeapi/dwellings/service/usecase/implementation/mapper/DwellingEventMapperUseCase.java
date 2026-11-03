package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingDomain;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;

@Mapper(componentModel = "spring")
public interface DwellingEventMapperUseCase {

	@Mapping(target = "dwellingId", source = "id")
	DwellingUpdatedEvent toEvent(DwellingDomain domain);

}
