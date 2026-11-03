package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalogTestSupport;

class ParameterCatalogConfigurationTest {

	private static final String FORMAT = "smartlife.visits.validation.format.";
	private static final String LENGTH = "smartlife.visits.validation.length.";

	private final Map<String, Object> properties = new HashMap<>();
	private final StandardEnvironment environment = new StandardEnvironment();

	@BeforeEach
	void setUp() {
		for (final String field : new String[] { "document-number", "first-name", "middle-name", "last-name",
				"second-last-name", "email", "vehicle-plate" }) {
			properties.put(FORMAT + field, "^[A-Za-z0-9]+$");
			properties.put(LENGTH + field + "-min", "2");
			properties.put(LENGTH + field + "-max", "40");
		}
		properties.put(LENGTH + "observation-max", "300");
		properties.put(FORMAT + "document-signatures", "application/pdf=25504446, image/png=89504E47");
		environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
		ParameterCatalogTestSupport.unload();
	}

	@AfterEach
	void tearDown() {
		ParameterCatalogTestSupport.unload();
	}

	private void load() {
		new ParameterCatalogConfiguration(environment).loadParameters();
	}

	@Test
	void shouldLoadEveryParameterFromTheEnvironment() {
		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertDoesNotThrow(ParameterCatalog::ensureLoaded);
		assertEquals("^[A-Za-z0-9]+$", ParameterCatalog.EMAIL_PATTERN);
		assertEquals(2, ParameterCatalog.FIRST_NAME_MIN_LENGTH);
		assertEquals(40, ParameterCatalog.VEHICLE_PLATE_MAX_LENGTH);
		assertEquals(300, ParameterCatalog.OBSERVATION_MAX_LENGTH);
		assertArrayEquals(new byte[] { '%', 'P', 'D', 'F' }, ParameterCatalog.DOCUMENT_SIGNATURES.get("application/pdf"));
		assertArrayEquals(new byte[] { (byte) 0x89, 'P', 'N', 'G' }, ParameterCatalog.DOCUMENT_SIGNATURES.get("image/png"));
	}

	@Test
	void shouldNameEveryMissingOrInvalidParameterAndFailWhenTheyAreUsed() {
		properties.remove(LENGTH + "email-max");
		properties.put(FORMAT + "first-name", "([");
		properties.put(LENGTH + "observation-max", "muchos");
		properties.put(FORMAT + "document-signatures", "application/pdf=XYZ");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(LENGTH + "email-max"));
		assertTrue(exception.getMessage().contains(FORMAT + "first-name (expresion regular invalida)"));
		assertTrue(exception.getMessage().contains(LENGTH + "observation-max (no es un numero entero)"));
		assertTrue(exception.getMessage().contains(FORMAT + "document-signatures"));
	}

	@Test
	void shouldRejectALengthRangeThatMakesNoSense() {
		properties.put(LENGTH + "email-min", "10");
		properties.put(LENGTH + "email-max", "5");
		properties.put(LENGTH + "first-name-min", "-1");
		properties.put(LENGTH + "vehicle-plate-max", "0");
		properties.put(LENGTH + "observation-max", "0");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(LENGTH + "email-max (debe ser al menos 1 y no menor que el minimo)"));
		assertTrue(exception.getMessage().contains(LENGTH + "first-name-min (no puede ser negativo)"));
		assertTrue(exception.getMessage().contains(LENGTH + "vehicle-plate-max"));
		assertTrue(exception.getMessage().contains(LENGTH + "observation-max (debe ser al menos 1)"));
	}

	@Test
	void shouldStoreTheSignatureTypesInLowerCase() {
		properties.put(FORMAT + "document-signatures", "Application/PDF=25504446");

		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertArrayEquals(new byte[] { '%', 'P', 'D', 'F' }, ParameterCatalog.DOCUMENT_SIGNATURES.get("application/pdf"));
	}

	@Test
	void shouldKeepThePreviousValuesWhenARefreshArrivesIncomplete() {
		load();
		properties.remove(FORMAT + "email");
		properties.put(LENGTH + "observation-max", "999");

		new ParameterCatalogConfiguration(environment).onParametersRefreshed();

		assertTrue(ParameterCatalog.isLoaded());
		assertEquals(300, ParameterCatalog.OBSERVATION_MAX_LENGTH);
	}

	@Test
	void shouldPickUpTheNewValuesWhenARefreshArrivesComplete() {
		load();
		properties.put(LENGTH + "observation-max", "450");

		new ParameterCatalogConfiguration(environment).onParametersRefreshed();

		assertEquals(450, ParameterCatalog.OBSERVATION_MAX_LENGTH);
	}

}
