package co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.users.domain.UserDomain;
import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;

@Mapper(componentModel = "spring")
public interface UserEventMapperUseCase {

	@Mapping(target = "userId", source = "id")
	UserUpdatedEvent toEvent(UserDomain domain);

}
