package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import co.com.smartlife.smartlifeapi.shared.service.input.port.MessageResolverInputPort;

class SecurityErrorResponseWriterTest {

	private static final String SESSION_MESSAGE = "Tu sesi\u00f3n no es v\u00e1lida o expir\u00f3. Inicia sesi\u00f3n de nuevo.";
	private static final String DENIED_MESSAGE = "No tienes permiso para realizar esta acci\u00f3n.";
	private static final String GENERAL_MESSAGE = "Ocurri\u00f3 un error al procesar tu solicitud. Intenta de nuevo.";

	private static MessageResolverInputPort catalog() {
		return (code, fallback) -> switch (code) {
			case SecurityErrorResponseWriter.SESSION_REQUIRED_CODE -> SESSION_MESSAGE;
			case SecurityErrorResponseWriter.ACCESS_DENIED_CODE -> DENIED_MESSAGE;
			case SecurityErrorResponseWriter.GENERAL_ERROR_CODE -> GENERAL_MESSAGE;
			default -> fallback;
		};
	}

	private static String bodyAsUtf8(final MockHttpServletResponse response) throws Exception {
		return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
	}

	@Test
	void unauthorizedShouldAnswer401WithTheSessionMessageOfTheCatalogInUtf8() throws Exception {
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(catalog()).unauthorized(response);

		assertEquals(401, response.getStatus());
		assertTrue(response.getContentType().startsWith("application/json"));
		assertEquals("UTF-8", response.getCharacterEncoding());
		assertEquals("{\"messages\":[\"" + SESSION_MESSAGE + "\"],\"data\":[],\"responseSucceded\":false}",
				bodyAsUtf8(response));
	}

	@Test
	void forbiddenShouldAnswer403WithTheAccessDeniedMessageOfTheCatalog() throws Exception {
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(catalog()).forbidden(response);

		assertEquals(403, response.getStatus());
		assertEquals("{\"messages\":[\"" + DENIED_MESSAGE + "\"],\"data\":[],\"responseSucceded\":false}",
				bodyAsUtf8(response));
	}

	@Test
	void theAccentsShouldTravelAsUtf8Bytes() throws Exception {
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(catalog()).unauthorized(response);

		assertTrue(bodyAsUtf8(response).contains("sesi\u00f3n"));
		assertTrue(response.getContentAsByteArray().length > SESSION_MESSAGE.length());
	}

	@Test
	void itShouldAskTheCatalogForTheSessionCodeFirst() throws Exception {
		final List<String> asked = new ArrayList<>();
		final MessageResolverInputPort recording = (code, fallback) -> {
			asked.add(code);
			return SESSION_MESSAGE;
		};

		new SecurityErrorResponseWriter(recording).unauthorized(new MockHttpServletResponse());

		assertEquals(List.of(SecurityErrorResponseWriter.SESSION_REQUIRED_CODE), asked);
	}

	@Test
	void whenTheSpecificMessageIsMissingTheGeneralMessageOfTheCatalogIsUsed() throws Exception {
		final MessageResolverInputPort onlyGeneral = (code, fallback) -> SecurityErrorResponseWriter.GENERAL_ERROR_CODE
				.equals(code) ? GENERAL_MESSAGE : fallback;
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(onlyGeneral).unauthorized(response);

		assertEquals(401, response.getStatus());
		assertEquals("{\"messages\":[\"" + GENERAL_MESSAGE + "\"],\"data\":[],\"responseSucceded\":false}",
				bodyAsUtf8(response));
	}

	@Test
	void whenTheCatalogHasNothingTheMessagesAreEmptyButTheStatusIsKept() throws Exception {
		final MessageResolverInputPort empty = (code, fallback) -> fallback;
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(empty).forbidden(response);

		assertEquals(403, response.getStatus());
		assertEquals("{\"messages\":[],\"data\":[],\"responseSucceded\":false}", bodyAsUtf8(response));
	}

	@Test
	void aMessageWithQuotesAndBackslashesShouldStillProduceValidJson() throws Exception {
		final MessageResolverInputPort tricky = (code, fallback) -> "dice \"hola\" y \\ adios\nlinea";
		final MockHttpServletResponse response = new MockHttpServletResponse();

		new SecurityErrorResponseWriter(tricky).unauthorized(response);

		assertEquals("{\"messages\":[\"dice \\\"hola\\\" y \\\\ adios\\nlinea\"],\"data\":[],\"responseSucceded\":false}",
				bodyAsUtf8(response));
	}

	@Test
	void controlCharactersShouldBeEscapedAsUnicode() {
		assertEquals("a\\u0001b\\tc", SecurityErrorResponseWriter.escape("a\u0001b\tc"));
	}
}
