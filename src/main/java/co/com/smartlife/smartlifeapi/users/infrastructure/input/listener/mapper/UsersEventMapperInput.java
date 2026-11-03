package co.com.smartlife.smartlifeapi.users.infrastructure.input.listener.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.RefreshUsersViewsDtoRequest;

/** Evento de otro modulo hacia la peticion que entiende este modulo. */
@Mapper(componentModel = "spring")
public interface UsersEventMapperInput {

	@Mapping(target = "source", constant = "URBANIZATION")
	RefreshUsersViewsDtoRequest fromUrbanization(final UrbanizationUpdatedEvent event);
}
