package co.com.smartlife.smartlifeapi.crosscutting.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SmarLifeExceptionTest {

	@Test
	void createWithOneMessageShouldUseItAsUserAndTechnicalMessage() {
		SmarLifeException exception = SmarLifeException.create("Algo salió mal");

		assertEquals("Algo salió mal", exception.getUserMessage());
		assertEquals("Algo salió mal", exception.getTechnicalMessage());
	}

	@Test
	void createWithTwoMessagesShouldKeepThemSeparate() {
		SmarLifeException exception = SmarLifeException.create("Mensaje para el usuario", "Detalle técnico");

		assertEquals("Mensaje para el usuario", exception.getUserMessage());
		assertEquals("Detalle técnico", exception.getTechnicalMessage());
	}

	@Test
	void theRootExceptionShouldNotBeNull() {
		SmarLifeException exception = SmarLifeException.create("Algo salió mal");

		assertNotNull(exception.getRootException());
		assertInstanceOf(Exception.class, exception.getRootException());
	}

	@Test
	void shouldBeThrowableAsARuntimeException() {
		assertThrows(RuntimeException.class, () -> {
			throw SmarLifeException.create("Error");
		});
	}

	@Test
	void serialVersionUidShouldBeOne() {
		assertEquals(1L, SmarLifeException.getSerialversionuid());
	}
}