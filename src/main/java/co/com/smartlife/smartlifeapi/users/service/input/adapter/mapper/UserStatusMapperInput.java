package co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.users.domain.UserStatusChangeDomain;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.ChangeUserStatusDtoRequest;

/** Peticion hacia dominio. El caso de uso no devuelve nada, asi que no hay mapeo de salida. */
@Mapper(componentModel = "spring")
public interface UserStatusMapperInput {

	UserStatusChangeDomain toDomain(ChangeUserStatusDtoRequest request);

}
