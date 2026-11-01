package co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeDomain;
import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeEventDomain;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;

/** DTO del webhook del repositorio de parametros a dominio. */
@Mapper(componentModel = "spring")
public interface ConfigWebhookMapperInput {

	@Mapping(target = "event", source = "event")
	@Mapping(target = "reference", source = "ref")
	ParametersChangeDomain toDomain(ConfigWebhookDtoRequest request);

	default ParametersChangeEventDomain toEvent(final String event) {
		return "push".equals(event) ? ParametersChangeEventDomain.PUSH : ParametersChangeEventDomain.OTHER;
	}
}
