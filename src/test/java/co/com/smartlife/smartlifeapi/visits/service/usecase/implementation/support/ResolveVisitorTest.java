package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
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

	/** El catálogo es estático: se deja como "sin configurar" (devuelve el código) para no afectar otras pruebas. */
	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catálogo de mensajes no configurado en la prueba");
		});
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

	@Test
	void shouldRejectANewVisitorWhoseEmailBelongsToAnotherVisitor() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
		final VisitorEntity owner = new VisitorEntity(UUID.randomUUID(), "Luis", "", "Torres", "", "2001",
				"luis@example.com", null);
		when(repository.findByEmail("luis@example.com")).thenReturn(Optional.of(owner));

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> resolveVisitor.resolve(visitor("9999", "Ana", "", "Perez", "", "  LUIS@Example.COM ", "")));

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		assertEquals("texto:USER_MESSAGE_VISITOR_EMAIL_ALREADY_REGISTERED", exception.getUserMessage());
		assertEquals("texto:TECHNICAL_MESSAGE_VISITOR_EMAIL_ALREADY_REGISTERED", exception.getTechnicalMessage());
		verify(repository).findByEmail("luis@example.com");
	}

	@Test
	void shouldNotLookUpTheEmailWhenTheDocumentAlreadyExists() {
		final VisitorEntity existing = new VisitorEntity(UUID.randomUUID(), "Luis", "", "Torres", "", "2001",
				"luis@example.com", null);
		when(repository.findByDocumentNumber("2001")).thenReturn(Optional.of(existing));

		resolveVisitor.resolve(visitor("2001", "", "", "", "", "otro@example.com", ""));

		verify(repository, never()).findByEmail(anyString());
	}

	@Test
	void shouldTakeBothMessagesOfEveryRejectionFromTheCatalog() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));

		assertCatalogRejection("VISITOR_DOCUMENT_REQUIRED", visitor("   ", "Luis", "", "Torres", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_DOCUMENT_INVALID", visitor("ab", "Luis", "", "Torres", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_FIRST_NAME_REQUIRED", visitor("2001", "   ", "", "Torres", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_FIRST_NAME_INVALID", visitor("2001", "Luis1", "", "Torres", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_LAST_NAME_REQUIRED", visitor("2001", "Luis", "", "", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_LAST_NAME_INVALID", visitor("2001", "Luis", "", "Tor3es", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_MIDDLE_NAME_INVALID", visitor("2001", "Luis", "Ma3", "Torres", "", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_SECOND_LAST_NAME_INVALID", visitor("2001", "Luis", "", "Torres", "Gom3z", "luis@example.com", ""));
		assertCatalogRejection("VISITOR_EMAIL_REQUIRED", visitor("2001", "Luis", "", "Torres", "", "   ", ""));
		assertCatalogRejection("VISITOR_EMAIL_INVALID", visitor("2001", "Luis", "", "Torres", "", "no-es-un-correo", ""));
		assertCatalogRejection("VISITOR_PLATE_INVALID", visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "ab"));
	}

	@Test
	void aRequiredFieldShouldGetADifferentMessageThanAnInvalidOne() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));

		final SmartLifeException empty = assertThrows(SmartLifeException.class,
				() -> resolveVisitor.resolve(visitor("2001", "", "", "Torres", "", "luis@example.com", "")));
		final SmartLifeException malformed = assertThrows(SmartLifeException.class,
				() -> resolveVisitor.resolve(visitor("2001", "Luis1", "", "Torres", "", "luis@example.com", "")));

		assertNotEquals(empty.getUserMessage(), malformed.getUserMessage());
		assertNotEquals(empty.getTechnicalMessage(), malformed.getTechnicalMessage());
	}

	private void assertCatalogRejection(final String code, final VisitorDomain data) {
		final SmartLifeException exception = assertThrows(SmartLifeException.class, () -> resolveVisitor.resolve(data));

		assertEquals("texto:USER_MESSAGE_" + code, exception.getUserMessage());
		assertEquals("texto:TECHNICAL_MESSAGE_" + code, exception.getTechnicalMessage());
	}
}
