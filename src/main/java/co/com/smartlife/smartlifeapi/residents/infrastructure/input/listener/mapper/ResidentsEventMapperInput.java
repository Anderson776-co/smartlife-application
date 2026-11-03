package co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.RefreshResidentsViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface ResidentsEventMapperInput {

	@Mapping(target = "source", constant = "USERS")
	RefreshResidentsViewsDtoRequest fromUsers(final UserUpdatedEvent event);

	@Mapping(target = "source", constant = "DWELLINGS")
	RefreshResidentsViewsDtoRequest fromDwellings(final DwellingUpdatedEvent event);
}
