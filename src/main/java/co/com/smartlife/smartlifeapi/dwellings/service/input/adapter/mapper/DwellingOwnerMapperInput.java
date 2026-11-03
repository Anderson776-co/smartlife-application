package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingOwnerChangeDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;

/** Peticion hacia dominio. El caso de uso no devuelve nada, asi que no hay mapeo de salida. */
@Mapper(componentModel = "spring")
public interface DwellingOwnerMapperInput {

	DwellingOwnerChangeDomain toDomain(ChangeDwellingOwnerDtoRequest request);

}
