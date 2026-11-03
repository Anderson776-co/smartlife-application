package co.com.smartlife.smartlifeapi.urbanization.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationRenameDomain;
import co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request.RenameUrbanizationDtoRequest;

/** Peticion hacia dominio. El caso de uso no devuelve nada, asi que no hay mapeo de salida. */
@Mapper(componentModel = "spring")
public interface UrbanizationRenameMapperInput {

	UrbanizationRenameDomain toDomain(RenameUrbanizationDtoRequest request);

}
