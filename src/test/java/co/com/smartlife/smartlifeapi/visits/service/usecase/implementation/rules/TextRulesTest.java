package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

class TextRulesTest {

	private static final String FIELD = "VISITOR_EMAIL";

	@BeforeEach
	void setUp() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private static void assertRejectedWith(final String expectedCode, final Executable executable) {
		final SmartLifeException exception = assertThrows(SmartLifeException.class, executable);

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		assertEquals("texto:USER_MESSAGE_" + expectedCode, exception.getUserMessage());
		assertEquals("texto:TECHNICAL_MESSAGE_" + expectedCode, exception.getTechnicalMessage());
	}

	@Test
	void textIsPresentShouldRejectANullOrBlankTextAsRequired() {
		assertRejectedWith(FIELD + "_REQUIRED", () -> TextIsPresent.executeRule(null, FIELD));
		assertRejectedWith(FIELD + "_REQUIRED", () -> TextIsPresent.executeRule("", FIELD));
		assertRejectedWith(FIELD + "_REQUIRED", () -> TextIsPresent.executeRule("   ", FIELD));
	}

	@Test
	void textIsPresentShouldAcceptAnyTextThatWasEntered() {
		assertDoesNotThrow(() -> TextIsPresent.executeRule("no-es-un-correo", FIELD));
	}

	@Test
	void textFormatIsValidShouldRejectATextThatDoesNotMatchAsInvalid() {
		assertRejectedWith(FIELD + "_INVALID", () -> TextFormatIsValid.executeRule("abc1", "^[a-z]+$", FIELD));
	}

	@Test
	void textFormatIsValidShouldIgnoreTheSpacesAtTheEnds() {
		assertDoesNotThrow(() -> TextFormatIsValid.executeRule("  abc ", "^[a-z]+$", FIELD));
	}

	@Test
	void textLengthIsValidShouldRejectATextOutsideTheRangeAsInvalid() {
		assertRejectedWith(FIELD + "_INVALID", () -> TextLengthIsValid.executeRule("abcdef", 1, 5, FIELD));
		assertRejectedWith(FIELD + "_INVALID", () -> TextLengthIsValid.executeRule("", 1, 5, FIELD));
		assertDoesNotThrow(() -> TextLengthIsValid.executeRule("abcde", 1, 5, FIELD));
	}

	@Test
	void aMissingTextAndAMalformedTextShouldNeverGetTheSameMessage() {
		final SmartLifeException missing = assertThrows(SmartLifeException.class,
				() -> TextIsPresent.executeRule("", FIELD));
		final SmartLifeException malformed = assertThrows(SmartLifeException.class,
				() -> TextFormatIsValid.executeRule("abc1", "^[a-z]+$", FIELD));

		assertEquals("texto:USER_MESSAGE_VISITOR_EMAIL_REQUIRED", missing.getUserMessage());
		assertEquals("texto:USER_MESSAGE_VISITOR_EMAIL_INVALID", malformed.getUserMessage());
	}
}
