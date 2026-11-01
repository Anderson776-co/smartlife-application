package co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeDomain;
import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeEventDomain;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;

class ConfigWebhookMapperInputTest {

	private final ConfigWebhookMapperInput mapper = Mappers.getMapper(ConfigWebhookMapperInput.class);

	@Test
	void shouldMapAPush() {
		final ParametersChangeDomain domain = mapper.toDomain(new ConfigWebhookDtoRequest("push", "refs/heads/main"));

		assertEquals(ParametersChangeEventDomain.PUSH, domain.event());
		assertEquals("refs/heads/main", domain.reference());
	}

	@Test
	void shouldMapAnyOtherEventAsOther() {
		assertEquals(ParametersChangeEventDomain.OTHER,
				mapper.toDomain(new ConfigWebhookDtoRequest("ping", "")).event());
		assertEquals(ParametersChangeEventDomain.OTHER, mapper.toDomain(new ConfigWebhookDtoRequest(null, null)).event());
	}
}
