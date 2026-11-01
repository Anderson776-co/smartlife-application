package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class ResolveVisitorTest {

	private VisitorRepository repository;
	private ResolveVisitor resolveVisitor;

	@BeforeEach
	void setUp() {
		repository = mock(VisitorRepository.class);
		when(repository.findByDocumentNumber(anyString())).thenReturn(Optional.empty());
		resolveVisitor = new ResolveVisitor(repository);
	}

	private static VisitorDomain visitor(final String document, final String first, final String middle,
			final String last, final String secondLast, final String email, final String plate) {
		return new VisitorDomain(null, document, first, middle, last, secondLast, email, plate);
	}

	private static VisitorDomain validVisitor() {
		return visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "");
	}

	private void assertRejected(final VisitorDomain data) {
		assertThrows(SmartLifeException.class, () -> resolveVisitor.resolve(data));
	}

	@Test
	void shouldRejectMissingData() {
		assertRejected(null);
		verify(repository, never()).findByDocumentNumber(anyString());
	}

	@Test
	void shouldRejectAnEmptyOrMalformedDocument() {
		assertRejected(visitor("   ", "Luis", "", "Torres", "", "luis@example.com", ""));
		assertRejected(visitor("ab", "Luis", "", "Torres", "", "luis@example.com", ""));
		assertRejected(visitor("ab cd 12", "Luis", "", "Torres", "", "luis@example.com", ""));
		verify(repository, never()).findByDocumentNumber(anyString());
	}

	@Test
	void shouldReturnTheExistingVisitorWithoutValidatingItsOtherData() {
		final VisitorEntity existing = new VisitorEntity(UUID.randomUUID(), "Luis", "", "Torres", "", "2001",
				"luis@example.com", null);
		when(repository.findByDocumentNumber("2001")).thenReturn(Optional.of(existing));

		final VisitorResolution resolution = resolveVisitor.resolve(visitor(" 2001 ", "", "", "", "", "", ""));

		assertFalse(resolution.isNew());
		assertSame(existing, resolution.entity());
	}

	@Test
	void shouldBuildANewVisitorWithNormalizedData() {
		final VisitorResolution resolution = resolveVisitor
				.resolve(visitor(" 2001 ", " Luis ", " Andres ", " Torres ", " Gomez ", "  LUIS@Example.COM ", " abc-123 "));

		assertTrue(resolution.isNew());
		final VisitorEntity entity = resolution.entity();
		assertNotNull(entity.getId());
		assertEquals("2001", entity.getDocumentNumber());
		assertEquals("Luis", entity.getFirstName());
		assertEquals("Andres", entity.getMiddleName());
		assertEquals("Torres", entity.getLastName());
		assertEquals("Gomez", entity.getSecondLastName());
		assertEquals("luis@example.com", entity.getEmail());
		assertEquals("ABC-123", entity.getVehiclePlate());
	}

	@Test
	void shouldLeaveThePlateEmptyWhenNoneIsGiven() {
		final VisitorResolution resolution = resolveVisitor.resolve(validVisitor());

		assertTrue(resolution.isNew());
		assertNull(resolution.entity().getVehiclePlate());
	}

	@Test
	void shouldRejectInvalidNamesForANewVisitor() {
		assertRejected(visitor("2001", "Luis1", "", "Torres", "", "luis@example.com", ""));
		assertRejected(visitor("2001", "", "", "Torres", "", "luis@example.com", ""));
		assertRejected(visitor("2001", "Luis", "", "", "", "luis@example.com", ""));
		assertRejected(visitor("2001", "Luis", "Ma3", "Torres", "", "luis@example.com", ""));
		assertRejected(visitor("2001", "Luis", "", "Torres", "Gom3z", "luis@example.com", ""));
	}

	@Test
	void shouldRejectAnInvalidEmailOrPlateForANewVisitor() {
		assertRejected(visitor("2001", "Luis", "", "Torres", "", "no-es-un-correo", ""));
		assertRejected(visitor("2001", "Luis", "", "Torres", "", "", ""));
		assertRejected(visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "ab"));
		assertRejected(visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "AB#123"));
	}
}