package co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.listener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.RefreshDwellingsViewsDtoRequest;

/** Evento de otro modulo hacia la peticion que entiende este modulo. */
@Mapper(componentModel = "spring")
public interface DwellingsEventMapperInput {

	@Mapping(target = "source", constant = "USERS")
	RefreshDwellingsViewsDtoRequest fromUsers(final UserUpdatedEvent event);

	@Mapping(target = "source", constant = "URBANIZATION")
	RefreshDwellingsViewsDtoRequest fromUrbanization(final UrbanizationUpdatedEvent event);
}
