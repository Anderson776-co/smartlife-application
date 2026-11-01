package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;

class GlobalExceptionHandlerRequestErrorsTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler((code, fallback) -> "catalogo:" + code);

	private static String message(final ResponseEntity<Response<Object>> response) {
		return response.getBody().getMessages().get(0);
	}

	@Test
	void uploadTooLargeShouldAnswer413() {
		ResponseEntity<Response<Object>> response = handler.handleUploadTooLarge(new MaxUploadSizeExceededException(10));

		assertEquals(413, response.getStatusCode().value());
		assertFalse(response.getBody().isResponseSucceded());
		assertEquals("catalogo:USER_MESSAGE_PAYLOAD_TOO_LARGE", message(response));
	}

	@Test
	void unreadableBodyShouldAnswer400() {
		ResponseEntity<Response<Object>> response = handler
				.handleUnreadable(new HttpMessageNotReadableException("JSON inválido", (HttpInputMessage) null));

		assertEquals(400, response.getStatusCode().value());
		assertEquals("catalogo:USER_MESSAGE_REQUEST_INVALID", message(response));
	}

	@Test
	void accessDeniedShouldAnswer403InsteadOfBecoming500() {
		ResponseEntity<Response<Object>> response = handler.handleAccessDenied(new AccessDeniedException("sin rol"));

		assertEquals(403, response.getStatusCode().value());
		assertEquals("catalogo:USER_MESSAGE_ACCESS_DENIED", message(response));
	}

	@Test
	void authenticationErrorShouldAnswer401() {
		ResponseEntity<Response<Object>> response = handler.handleAuthentication(new BadCredentialsException("sin sesión"));

		assertEquals(401, response.getStatusCode().value());
		assertEquals("catalogo:USER_MESSAGE_SESSION_REQUIRED", message(response));
	}

	@Test
	void clientErrorsThatSpringClassifiesShouldKeepTheir4xx() {
		ResponseEntity<Response<Object>> response = handler
				.handleUnexpected(new HttpRequestMethodNotSupportedException("PATCH"));

		assertEquals(405, response.getStatusCode().value());
		assertEquals("catalogo:USER_MESSAGE_REQUEST_INVALID", message(response));
	}

	@Test
	void anyOtherExceptionShouldAnswer500WithTheGenericCatalogMessage() {
		ResponseEntity<Response<Object>> response = handler.handleUnexpected(new IllegalStateException("R2 sin configurar"));

		assertEquals(500, response.getStatusCode().value());
		assertFalse(response.getBody().isResponseSucceded());
		assertEquals("catalogo:USER_ERROR_TRYING_TO_MAKE_AN_OPERATION", message(response));
	}

	@Test
	void shouldAnswerWithTheFallbackTextWhenTheCatalogCannotResolve() {
		final GlobalExceptionHandler withoutCatalog = new GlobalExceptionHandler((code, fallback) -> fallback);

		ResponseEntity<Response<Object>> response = withoutCatalog.handleUnexpected(new RuntimeException("boom"));

		assertEquals(500, response.getStatusCode().value());
		assertEquals("Se ha presentado un problema tratando de realizar la operación. Por favor intente de nuevo.",
				message(response));
	}
}
