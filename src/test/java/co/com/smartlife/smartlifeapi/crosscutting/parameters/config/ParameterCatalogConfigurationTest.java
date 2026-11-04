package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	private static final String VISITS = "smartlife.visits.";

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
		properties.put(VISITS + "max-authorization-duration", "12h");
		properties.put(VISITS + "max-anticipation", "7d");
		properties.put(VISITS + "start-tolerance", "5m");
		properties.put(VISITS + "document.max-size", "2MB");
		properties.put(VISITS + "document.allowed-types", "application/pdf,image/png");
		properties.put(VISITS + "qr-base-url", "https://app.smartlife.co/visitas/");
		properties.put(VISITS + "time-zone", "America/Bogota");
		properties.put(VISITS + "document.storage-key-format", "visits/authorizations/%s/document.%s");
		properties.put(VISITS + "document.default-extension", "bin");
		properties.put(VISITS + "document.extensions", "application/pdf=pdf,image/jpeg=jpg,image/png=png");
		properties.put(VISITS + "qr-content-id", "visit-qr");
		properties.put(VISITS + "qr-file-name", "autorizacion-visita.png");
		properties.put(VISITS + "qr-content-type", "image/png");
		properties.put(VISITS + "notification-date-pattern", "dd/MM/yyyy hh:mm a");
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
		assertArrayEquals(new byte[] { '%', 'P', 'D', 'F' }, ParameterCatalog.DOCUMENT_SIGNATURES.get().get("application/pdf"));
		assertArrayEquals(new byte[] { (byte) 0x89, 'P', 'N', 'G' }, ParameterCatalog.DOCUMENT_SIGNATURES.get().get("image/png"));
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
		assertArrayEquals(new byte[] { '%', 'P', 'D', 'F' }, ParameterCatalog.DOCUMENT_SIGNATURES.get().get("application/pdf"));
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

	@Test
	void shouldLoadTheVisitBusinessParameters() {
		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertEquals(Duration.ofHours(12), ParameterCatalog.MAX_AUTHORIZATION_DURATION);
		assertEquals(Duration.ofDays(7), ParameterCatalog.MAX_ANTICIPATION);
		assertEquals(Duration.ofMinutes(5), ParameterCatalog.START_TOLERANCE);
		assertEquals(2L * 1024 * 1024, ParameterCatalog.DOCUMENT_MAX_SIZE_IN_BYTES);
		assertEquals(Set.of("application/pdf", "image/png"), ParameterCatalog.DOCUMENT_ALLOWED_TYPES);
		assertEquals("https://app.smartlife.co/visitas/", ParameterCatalog.QR_BASE_URL);
		assertEquals(ZoneId.of("America/Bogota"), ParameterCatalog.TIME_ZONE);
	}

	@Test
	void shouldCompareTheAllowedTypesInLowerCase() {
		properties.put(VISITS + "document.allowed-types", "Application/PDF, IMAGE/png");

		load();

		assertEquals(Set.of("application/pdf", "image/png"), ParameterCatalog.DOCUMENT_ALLOWED_TYPES);
	}

	@Test
	void shouldNameEveryVisitParameterWhenNothingIsConfigured() {
		properties.keySet().removeIf(key -> key.startsWith(VISITS));

		load();

		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		for (final String name : new String[] { "max-authorization-duration", "max-anticipation", "start-tolerance",
				"document.max-size", "document.allowed-types", "qr-base-url", "time-zone" }) {
			assertTrue(exception.getMessage().contains(VISITS + name), "no menciona " + name);
		}
	}

	@Test
	void shouldTreatBlankVisitValuesAsMissing() {
		properties.put(VISITS + "qr-base-url", "   ");
		properties.put(VISITS + "document.allowed-types", "");

		load();

		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(VISITS + "qr-base-url"));
		assertTrue(exception.getMessage().contains(VISITS + "document.allowed-types"));
	}

	@Test
	void shouldRejectVisitValuesThatMakeNoSense() {
		properties.put(VISITS + "max-authorization-duration", "0s");
		properties.put(VISITS + "max-anticipation", "-1d");
		properties.put(VISITS + "start-tolerance", "-5m");
		properties.put(VISITS + "document.max-size", "0B");
		properties.put(VISITS + "qr-base-url", "/visitas/autorizaciones/");
		properties.put(VISITS + "time-zone", "Mars/Olympus");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		for (final String name : new String[] { "max-authorization-duration", "max-anticipation", "start-tolerance",
				"document.max-size", "qr-base-url", "time-zone" }) {
			assertTrue(exception.getMessage().contains(VISITS + name), "no menciona " + name);
		}
	}

	@Test
	void shouldNameAVisitValueThatCannotBeRead() {
		properties.put(VISITS + "max-anticipation", "treinta dias");

		load();

		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(VISITS + "max-anticipation (valor invalido)"));
	}

	@Test
	void shouldPickUpTheNewVisitValuesWhenARefreshArrivesComplete() {
		load();
		properties.put(VISITS + "max-anticipation", "3d");

		new ParameterCatalogConfiguration(environment).onParametersRefreshed();

		assertEquals(Duration.ofDays(3), ParameterCatalog.MAX_ANTICIPATION);
	}

	@Test
	void shouldLoadTheNotificationAndDocumentStorageParameters() {
		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertEquals("visit-qr", ParameterCatalog.QR_CONTENT_ID);
		assertEquals("autorizacion-visita.png", ParameterCatalog.QR_FILE_NAME);
		assertEquals("image/png", ParameterCatalog.QR_CONTENT_TYPE);
		assertEquals("dd/MM/yyyy hh:mm a", ParameterCatalog.NOTIFICATION_DATE_PATTERN);
		assertEquals("visits/authorizations/%s/document.%s", ParameterCatalog.DOCUMENT_STORAGE_KEY_FORMAT);
		assertEquals("bin", ParameterCatalog.DOCUMENT_DEFAULT_EXTENSION);
		assertEquals(Map.of("application/pdf", "pdf", "image/jpeg", "jpg", "image/png", "png"),
				ParameterCatalog.DOCUMENT_EXTENSIONS);
	}

	@Test
	void shouldStoreTheDocumentExtensionsInLowerCaseAndTrimTheDefaultExtension() {
		properties.put(VISITS + "document.extensions", "Application/PDF=pdf");
		properties.put(VISITS + "document.default-extension", " bin ");

		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertEquals(Map.of("application/pdf", "pdf"), ParameterCatalog.DOCUMENT_EXTENSIONS);
		assertEquals("bin", ParameterCatalog.DOCUMENT_DEFAULT_EXTENSION);
	}

	@Test
	void shouldAcceptAQrBaseUrlThatUsesPlainHttp() {
		properties.put(VISITS + "qr-base-url", "http://localhost:5173/visitas/autorizaciones/");

		load();

		assertTrue(ParameterCatalog.isLoaded());
		assertEquals("http://localhost:5173/visitas/autorizaciones/", ParameterCatalog.QR_BASE_URL);
	}

	@Test
	void shouldRejectAQrBaseUrlWithASchemeThatIsNotHttp() {
		properties.put(VISITS + "qr-base-url", "ftp://files.smartlife.co/visitas/");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(VISITS + "qr-base-url (debe ser una URL http o https completa)"));
	}

	@Test
	void shouldRejectAQrBaseUrlThatIsNotAValidUri() {
		properties.put(VISITS + "qr-base-url", "http://exa mple.com/visitas/");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(VISITS + "qr-base-url (debe ser una URL http o https completa)"));
	}

	@Test
	void shouldRejectANotificationDatePatternThatCannotBeUsed() {
		properties.put(VISITS + "notification-date-pattern", "dd/MM/yyyy {hh}");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(VISITS + "notification-date-pattern (patron de fecha invalido"));
	}

	@Test
	void shouldRejectAStorageKeyFormatThatCannotReceiveTheIdAndTheExtension() {
		for (final String value : new String[] { "visits/%s/%s/%s", "visits/%d/document.%s", "visits/%q" }) {
			properties.put(VISITS + "document.storage-key-format", value);

			load();

			final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
			assertTrue(exception.getMessage().contains(VISITS + "document.storage-key-format (formato invalido"),
					"no rechazo " + value);
		}
	}

	@Test
	void shouldRejectEveryMalformedEntryOfTheDocumentExtensions() {
		for (final String value : new String[] { "application/pdf", "=pdf", "application/pdf=",
				"application/pdf=pdf,image/png" }) {
			properties.put(VISITS + "document.extensions", value);

			load();

			final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
			assertTrue(exception.getMessage().contains(VISITS + "document.extensions (entrada invalida"),
					"no rechazo " + value);
		}
	}

	@Test
	void shouldRejectEveryMalformedEntryOfTheDocumentSignatures() {
		for (final String value : new String[] { "application/pdf", "=25504446", "application/pdf=25504446,image/png" }) {
			properties.put(FORMAT + "document-signatures", value);

			load();

			final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
			assertTrue(exception.getMessage().contains(FORMAT + "document-signatures (entrada invalida"),
					"no rechazo " + value);
		}
	}

	@Test
	void shouldTreatBlankFormatLengthAndExtensionValuesAsMissing() {
		properties.put(FORMAT + "email", "   ");
		properties.put(LENGTH + "email-min", "  ");
		properties.put(FORMAT + "document-signatures", "");
		properties.put(VISITS + "document.extensions", " ");

		load();

		assertFalse(ParameterCatalog.isLoaded());
		final IllegalStateException exception = assertThrows(IllegalStateException.class, ParameterCatalog::ensureLoaded);
		assertTrue(exception.getMessage().contains(FORMAT + "email"));
		assertTrue(exception.getMessage().contains(LENGTH + "email-min"));
		assertTrue(exception.getMessage().contains(FORMAT + "document-signatures"));
		assertTrue(exception.getMessage().contains(VISITS + "document.extensions"));
	}

}
