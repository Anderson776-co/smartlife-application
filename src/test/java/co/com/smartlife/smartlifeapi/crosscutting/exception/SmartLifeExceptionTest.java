package co.com.smartlife.smartlifeapi.crosscutting.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException.Type;

class SmartLifeExceptionTest {

	@Test
	void createWithOneMessageShouldUseItAsUserAndTechnicalMessage() {
		SmartLifeException exception = SmartLifeException.create("Algo salió mal");

		assertEquals("Algo salió mal", exception.getUserMessage());
		assertEquals("Algo salió mal", exception.getTechnicalMessage());
	}

	@Test
	void createWithTwoMessagesShouldKeepThemSeparate() {
		SmartLifeException exception = SmartLifeException.create("Mensaje para el usuario", "Detalle técnico");

		assertEquals("Mensaje para el usuario", exception.getUserMessage());
		assertEquals("Detalle técnico", exception.getTechnicalMessage());
	}

	@Test
	void createWithMessagesShouldBeBusiness() {
		assertEquals(Type.BUSINESS, SmartLifeException.create("Error").getType());
		assertEquals(Type.BUSINESS, SmartLifeException.create("Usuario", "Técnico").getType());
	}

	@Test
	void theRootExceptionShouldNotBeNull() {
		SmartLifeException exception = SmartLifeException.create("Algo salió mal");

		assertNotNull(exception.getRootException());
		assertInstanceOf(Exception.class, exception.getRootException());
	}

	@Test
	void createWithRootExceptionShouldKeepItAndBeTechnical() {
		IllegalStateException root = new IllegalStateException("Fallo de conexión");

		SmartLifeException exception = SmartLifeException.create(root, "Mensaje para el usuario", "Detalle técnico");

		assertSame(root, exception.getRootException());
		assertEquals("Mensaje para el usuario", exception.getUserMessage());
		assertEquals("Detalle técnico", exception.getTechnicalMessage());
		assertEquals(Type.TECHNICAL, exception.getType());
	}

	@Test
	void aNullRootExceptionShouldBeReplacedByADefaultOne() {
		SmartLifeException exception = SmartLifeException.create((Throwable) null, "Usuario", "Técnico");

		assertNotNull(exception.getRootException());
	}

	@Test
	void createAuthenticationShouldBeAuthentication() {
		SmartLifeException exception = SmartLifeException.createAuthentication("Credenciales inválidas", "Token expirado");

		assertEquals(Type.AUTHENTICATION, exception.getType());
		assertEquals("Credenciales inválidas", exception.getUserMessage());
		assertEquals("Token expirado", exception.getTechnicalMessage());
		assertNotNull(exception.getRootException());
	}

	@Test
	void createAuthorizationShouldBeAuthorization() {
		SmartLifeException exception = SmartLifeException.createAuthorization("Sin permisos", "Rol insuficiente");

		assertEquals(Type.AUTHORIZATION, exception.getType());
		assertEquals("Sin permisos", exception.getUserMessage());
		assertEquals("Rol insuficiente", exception.getTechnicalMessage());
		assertNotNull(exception.getRootException());
	}

	@Test
	void nullMessagesShouldBecomeEmptyStrings() {
		SmartLifeException exception = SmartLifeException.create(null, null);

		assertEquals("", exception.getUserMessage());
		assertEquals("", exception.getTechnicalMessage());
	}

	@Test
	void getMessageShouldReturnTheTechnicalMessage() {
		SmartLifeException exception = SmartLifeException.create("Usuario", "Técnico");

		assertEquals("Técnico", exception.getMessage());
	}

	@Test
	void shouldBeThrowableAsARuntimeException() {
		assertThrows(RuntimeException.class, () -> {
			throw SmartLifeException.create("Error");
		});
	}

	@Test
	void serialVersionUidShouldBeOne() {
		assertEquals(1L, SmartLifeException.getSerialversionuid());
	}
}