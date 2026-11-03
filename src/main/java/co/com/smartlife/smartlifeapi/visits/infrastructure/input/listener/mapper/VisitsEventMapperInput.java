package co.com.smartlife.smartlifeapi.visits.infrastructure.input.listener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.event.ResidentResidenceUpdatedEvent;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.RefreshVisitsViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface VisitsEventMapperInput {

	@Mapping(target = "source", constant = "USERS")
	RefreshVisitsViewsDtoRequest fromUsers(final UserUpdatedEvent event);

	@Mapping(target = "source", constant = "DWELLINGS")
	RefreshVisitsViewsDtoRequest fromDwellings(final DwellingUpdatedEvent event);

	@Mapping(target = "source", constant = "RESIDENTS")
	RefreshVisitsViewsDtoRequest fromResidents(final ResidentResidenceUpdatedEvent event);
}
