package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class VisitAuthorizationSummaryMapperUseCaseTest {

	private static final Instant START = Instant.parse("2026-11-03T10:00:00Z");

	private final VisitAuthorizationSummaryMapperUseCase mapper = Mappers
			.getMapper(VisitAuthorizationSummaryMapperUseCase.class);

	private static VisitAuthorizationEntity entity(final PropertyVisitEntity property, final VisitReasonEntity reason,
			final VisitorEntity visitor, final String fileReference, final Boolean active) {
		return new VisitAuthorizationEntity(UUID.randomUUID(), START, START.plusSeconds(3600), START, "obs", "qr",
				fileReference, active, property, new AuthorizatorEntity(UUID.randomUUID(), "Maria", true, UUID.randomUUID()),
				visitor, reason);
	}

	private static VisitorEntity visitor(final String first, final String middle, final String last,
			final String secondLast, final String plate) {
		return new VisitorEntity(UUID.randomUUID(), first, middle, last, secondLast, "2001", "luis@example.com", plate);
	}

	@Test
	void theFullNameShouldSkipTheNamesThatAreMissingOrBlank() {
		final VisitAuthorizationSummaryDomain result = mapper.toDomain(entity(
				new PropertyVisitEntity(UUID.randomUUID(), "Casa 1"), new VisitReasonEntity(UUID.randomUUID(), "Visita", false),
				visitor("Luis", null, "Torres", "   ", "ABC123"), "", true));

		assertEquals("Luis Torres", result.visitorFullName());
	}

	@Test
	void theMissingPropertyReasonAndPlateShouldBecomeEmptyTexts() {
		final VisitAuthorizationSummaryDomain result = mapper
				.toDomain(entity(null, null, visitor("Luis", "", "Torres", "", null), null, false));

		assertEquals("", result.propertyLocation());
		assertEquals("", result.visitReason());
		assertEquals("", result.vehiclePlate());
		assertFalse(result.documentAttached());
		assertEquals(Boolean.FALSE, result.isActive());
	}

	@Test
	void aBlankFileReferenceShouldNotCountAsAnAttachedDocument() {
		final VisitAuthorizationSummaryDomain result = mapper.toDomain(entity(null, null,
				visitor("Luis", "", "Torres", "", "ABC123"), "   ", true));

		assertFalse(result.documentAttached());
	}

	@Test
	void aFileReferenceShouldCountAsAnAttachedDocument() {
		final VisitAuthorizationSummaryDomain result = mapper.toDomain(entity(null, null,
				visitor("Luis", "", "Torres", "", "ABC123"), "visits/x/document.png", true));

		assertTrue(result.documentAttached());
	}

	@Test
	void withoutVisitorTheFullNameShouldBeEmpty() {
		final VisitAuthorizationSummaryDomain result = mapper.toDomain(entity(null, null, null, "", true));

		assertEquals("", result.visitorFullName());
		assertNull(result.visitorEmail());
	}

	@Test
	void aNullEntityShouldMapToNull() {
		assertNull(mapper.toDomain(null));
	}
}
