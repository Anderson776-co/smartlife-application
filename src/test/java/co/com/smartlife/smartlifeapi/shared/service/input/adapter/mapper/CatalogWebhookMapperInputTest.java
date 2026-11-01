package co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogEventDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogInvalidationDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;

class CatalogWebhookMapperInputTest {

	private final CatalogWebhookMapperInput mapper = Mappers.getMapper(CatalogWebhookMapperInput.class);

	@Test
	void shouldMapAPublishedMessage() {
		final CatalogInvalidationDomain domain = mapper.toDomain(new CatalogWebhookDtoRequest("entry.publish",
				"message-catalog", new CatalogWebhookDtoRequest.Entry("MESSAGE_CODE", "es")));

		assertEquals(CatalogEventDomain.PUBLISH, domain.event());
		assertEquals(CatalogTypeDomain.MESSAGE, domain.catalog());
		assertEquals("MESSAGE_CODE", domain.code());
		assertEquals("es", domain.locale());
		assertTrue(domain.hasEntry());
		assertTrue(domain.requiresEviction());
	}

	@Test
	void shouldMapUnpublishAndDeleteOfANotification() {
		final CatalogInvalidationDomain unpublished = mapper.toDomain(new CatalogWebhookDtoRequest("entry.unpublish",
				"notification-catalog", new CatalogWebhookDtoRequest.Entry("NOTIFICATION_CODE", "es")));
		final CatalogInvalidationDomain deleted = mapper.toDomain(new CatalogWebhookDtoRequest("entry.delete",
				"notification-catalog", new CatalogWebhookDtoRequest.Entry("NOTIFICATION_CODE", "es")));

		assertEquals(CatalogEventDomain.UNPUBLISH, unpublished.event());
		assertEquals(CatalogEventDomain.DELETE, deleted.event());
		assertEquals(CatalogTypeDomain.NOTIFICATION, deleted.catalog());
	}

	@Test
	void shouldTreatOtherEventsAndUnknownModelsAsIgnorable() {
		final CatalogInvalidationDomain update = mapper.toDomain(new CatalogWebhookDtoRequest("entry.update",
				"message-catalog", new CatalogWebhookDtoRequest.Entry("MESSAGE_CODE", "es")));
		final CatalogInvalidationDomain unknown = mapper.toDomain(new CatalogWebhookDtoRequest("entry.publish",
				"otro-modelo", new CatalogWebhookDtoRequest.Entry("MESSAGE_CODE", "es")));

		assertEquals(CatalogEventDomain.OTHER, update.event());
		assertFalse(update.requiresEviction());
		assertEquals(CatalogTypeDomain.UNKNOWN, unknown.catalog());
		assertFalse(unknown.requiresEviction());
	}

	@Test
	void shouldReportAMissingEntry() {
		final CatalogInvalidationDomain domain = mapper
				.toDomain(new CatalogWebhookDtoRequest("entry.publish", "message-catalog", null));

		assertFalse(domain.hasEntry());
		assertTrue(domain.requiresEviction());
	}
}
