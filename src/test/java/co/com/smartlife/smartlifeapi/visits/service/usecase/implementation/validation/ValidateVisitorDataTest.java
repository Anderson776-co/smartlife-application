package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalogTestSupport;
import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;

class ValidateVisitorDataTest {

	@BeforeEach
	void setUp() {
		ParameterCatalogTestSupport.load();
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private static VisitorDomain visitor(final String document, final String first, final String middle,
			final String last, final String secondLast, final String email, final String plate) {
		return new VisitorDomain(null, document, first, middle, last, secondLast, email, plate);
	}

	private static void assertRejectedWith(final String expectedCode, final Executable executable) {
		final SmartLifeException exception = assertThrows(SmartLifeException.class, executable);

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		assertEquals("texto:USER_MESSAGE_" + expectedCode, exception.getUserMessage());
		assertEquals("texto:TECHNICAL_MESSAGE_" + expectedCode, exception.getTechnicalMessage());
	}

	private static void assertNewVisitorRejectedWith(final String expectedCode, final VisitorDomain data) {
		assertRejectedWith(expectedCode, () -> ValidateNewVisitorData.executeValidation(data));
	}

	@Test
	void theDocumentNumberShouldBeRequired() {
		assertRejectedWith("VISITOR_DOCUMENT_REQUIRED", () -> ValidateVisitorDocumentNumber.executeValidation((Object) null));
		assertRejectedWith("VISITOR_DOCUMENT_REQUIRED", () -> ValidateVisitorDocumentNumber.executeValidation(""));
		assertRejectedWith("VISITOR_DOCUMENT_REQUIRED", () -> ValidateVisitorDocumentNumber.executeValidation("   "));
	}

	@Test
	void theDocumentNumberShouldBeRejectedWhenMalformed() {
		assertRejectedWith("VISITOR_DOCUMENT_INVALID", () -> ValidateVisitorDocumentNumber.executeValidation("ab"));
		assertRejectedWith("VISITOR_DOCUMENT_INVALID", () -> ValidateVisitorDocumentNumber.executeValidation("ab cd 12"));
		assertRejectedWith("VISITOR_DOCUMENT_INVALID", () -> ValidateVisitorDocumentNumber.executeValidation("1".repeat(31)));
	}

	@Test
	void theDocumentNumberShouldBeAcceptedWhenWellFormed() {
		assertDoesNotThrow(() -> ValidateVisitorDocumentNumber.executeValidation("2001"));
		assertDoesNotThrow(() -> ValidateVisitorDocumentNumber.executeValidation(" 2001 "));
		assertDoesNotThrow(() -> ValidateVisitorDocumentNumber.executeValidation("AB-2001"));
	}

	@Test
	void aNewVisitorShouldRequireFirstNameLastNameAndEmail() {
		assertNewVisitorRejectedWith("VISITOR_FIRST_NAME_REQUIRED", visitor("2001", "", "", "Torres", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_FIRST_NAME_REQUIRED", visitor("2001", null, "", "Torres", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_LAST_NAME_REQUIRED", visitor("2001", "Luis", "", "  ", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_EMAIL_REQUIRED", visitor("2001", "Luis", "", "Torres", "", "", ""));
		assertNewVisitorRejectedWith("VISITOR_EMAIL_REQUIRED", visitor("2001", "Luis", "", "Torres", "", null, ""));
	}

	@Test
	void aNewVisitorShouldRejectMalformedData() {
		assertNewVisitorRejectedWith("VISITOR_FIRST_NAME_INVALID", visitor("2001", "Luis1", "", "Torres", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_LAST_NAME_INVALID", visitor("2001", "Luis", "", "Tor3es", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_MIDDLE_NAME_INVALID", visitor("2001", "Luis", "Ma3", "Torres", "", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_SECOND_LAST_NAME_INVALID", visitor("2001", "Luis", "", "Torres", "Gom3z", "luis@example.com", ""));
		assertNewVisitorRejectedWith("VISITOR_EMAIL_INVALID", visitor("2001", "Luis", "", "Torres", "", "no-es-un-correo", ""));
		assertNewVisitorRejectedWith("VISITOR_EMAIL_INVALID", visitor("2001", "Luis", "", "Torres", "", "a".repeat(250) + "@x.com", ""));
		assertNewVisitorRejectedWith("VISITOR_PLATE_INVALID", visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "ab"));
		assertNewVisitorRejectedWith("VISITOR_PLATE_INVALID", visitor("2001", "Luis", "", "Torres", "", "luis@example.com", "AB#123"));
	}

	@Test
	void theOptionalDataOfANewVisitorShouldNotBeRequired() {
		assertDoesNotThrow(() -> ValidateNewVisitorData
				.executeValidation(visitor("2001", "Luis", null, "Torres", null, "luis@example.com", null)));
		assertDoesNotThrow(() -> ValidateNewVisitorData
				.executeValidation(visitor("2001", "Luis", "  ", "Torres", "  ", "luis@example.com", "  ")));
	}

	@Test
	void aNewVisitorShouldAcceptTheDataInTheFormatThatIsStored() {
		assertDoesNotThrow(() -> ValidateNewVisitorData.executeValidation(
				visitor("2001", " Ñandú ", "Andrés", "O'Brien", "de la Cruz", "  LUIS@Example.COM ", " abc-123 ")));
	}

	@Test
	void aMissingDataShouldNeverGetTheMessageOfAMalformedOne() {
		final SmartLifeException missing = assertThrows(SmartLifeException.class, () -> ValidateNewVisitorData
				.executeValidation(visitor("2001", "", "", "Torres", "", "luis@example.com", "")));
		final SmartLifeException malformed = assertThrows(SmartLifeException.class, () -> ValidateNewVisitorData
				.executeValidation(visitor("2001", "Luis1", "", "Torres", "", "luis@example.com", "")));

		assertEquals("texto:USER_MESSAGE_VISITOR_FIRST_NAME_REQUIRED", missing.getUserMessage());
		assertEquals("texto:USER_MESSAGE_VISITOR_FIRST_NAME_INVALID", malformed.getUserMessage());
	}
}
