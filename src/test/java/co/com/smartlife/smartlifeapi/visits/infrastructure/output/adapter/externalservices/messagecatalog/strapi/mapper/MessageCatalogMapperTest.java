package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.messagecatalog.strapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.messagecatalog.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.MessageCatalogDTO;

class MessageCatalogMapperTest {

	private final MessageCatalogMapper mapper = Mappers.getMapper(MessageCatalogMapper.class);

	private final StrapiMessageCatalogData data = new StrapiMessageCatalogData("VISIT_AUTHORIZED",
			"Autorización registrada", "La autorización de visita fue registrada correctamente.", "es");

	private final MessageCatalogDTO dto = new MessageCatalogDTO("VISIT_AUTHORIZED", "Autorización registrada",
			"La autorización de visita fue registrada correctamente.", "es");

	@Test
	void toDtoShouldCopyEveryField() {
		MessageCatalogDTO result = mapper.toDTO(data);

		assertEquals("VISIT_AUTHORIZED", result.code());
		assertEquals("Autorización registrada", result.title());
		assertEquals("La autorización de visita fue registrada correctamente.", result.content());
		assertEquals("es", result.locale());
	}

	@Test
	void toDomainShouldCopyEveryField() {
		StrapiMessageCatalogData result = mapper.toDomain(dto);

		assertEquals(data, result);
	}

	@Test
	void toDtoShouldMapAList() {
		List<MessageCatalogDTO> result = mapper.toDTO(List.of(data));

		assertEquals(List.of(dto), result);
	}

	@Test
	void nullsShouldBeMappedToNull() {
		assertNull(mapper.toDTO((StrapiMessageCatalogData) null));
		assertNull(mapper.toDomain(null));
		assertNull(mapper.toDTO((List<StrapiMessageCatalogData>) null));
	}
}