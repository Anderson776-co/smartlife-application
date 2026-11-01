package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

class SpringVisitParametersAdapterTest {

	private static final String PREFIX = "smartlife.visits.";

	@Test
	void shouldBindTheConfiguredValues() {
		final StandardEnvironment environment = environmentWith(Map.of(
				"smartlife.visits.max-authorization-duration", "12h",
				"smartlife.visits.max-anticipation", "7d",
				"smartlife.visits.start-tolerance", "5m",
				"smartlife.visits.document.max-size", "2MB",
				"smartlife.visits.document.allowed-types", "application/pdf,image/png",
				"smartlife.visits.qr-base-url", "https://app.smartlife.co/visitas/",
				"smartlife.visits.time-zone", "America/Bogota"));

		final VisitParametersDTO parameters = new SpringVisitParametersAdapter(environment).get();

		assertEquals(Duration.ofHours(12), parameters.maxAuthorizationDuration());
		assertEquals(Duration.ofDays(7), parameters.maxAnticipation());
		assertEquals(Duration.ofMinutes(5), parameters.startTolerance());
		assertEquals(2L * 1024 * 1024, parameters.documentMaxSizeInBytes());
		assertEquals(Set.of("application/pdf", "image/png"), parameters.documentAllowedTypes());
		assertEquals(ZoneId.of("America/Bogota"), parameters.timeZone());
		final UUID id = UUID.randomUUID();
		assertEquals("https://app.smartlife.co/visitas/" + id, parameters.qrUrlFor(id));
	}

	@Test
	void shouldAddTheTrailingSlashToTheQrBaseUrl() {
		final Map<String, Object> properties = allParameters();
		properties.put(PREFIX + "qr-base-url", "http://host/visitas");

		final VisitParametersDTO parameters = new SpringVisitParametersAdapter(environmentWith(properties)).get();
		final UUID id = UUID.randomUUID();

		assertEquals("http://host/visitas/" + id, parameters.qrUrlFor(id));
	}

	@Test
	void shouldReflectPropertyChangesOnTheNextCall() {
		final Map<String, Object> properties = allParameters();
		properties.put(PREFIX + "max-anticipation", "7d");
		final StandardEnvironment environment = environmentWith(properties);
		final SpringVisitParametersAdapter adapter = new SpringVisitParametersAdapter(environment);
		assertEquals(Duration.ofDays(7), adapter.get().maxAnticipation());

		environment.getPropertySources()
				.addFirst(new MapPropertySource("refreshed", Map.of(PREFIX + "max-anticipation", "3d")));

		assertEquals(Duration.ofDays(3), adapter.get().maxAnticipation());
	}

	@Test
	void shouldFailListingEveryMissingParameterWhenNothingIsConfigured() {
		final SpringVisitParametersAdapter adapter = new SpringVisitParametersAdapter(environmentWith(Map.of()));

		final IllegalStateException failure = assertThrows(IllegalStateException.class, adapter::get);

		for (final String name : new String[] { "max-authorization-duration", "max-anticipation", "start-tolerance",
				"document.max-size", "document.allowed-types", "qr-base-url", "time-zone" }) {
			assertTrue(failure.getMessage().contains(PREFIX + name), "no menciona " + name);
		}
	}

	@Test
	void shouldFailOnlyAboutTheParameterThatIsMissing() {
		final Map<String, Object> properties = allParameters();
		properties.remove(PREFIX + "qr-base-url");

		final IllegalStateException failure = assertThrows(IllegalStateException.class,
				() -> new SpringVisitParametersAdapter(environmentWith(properties)).get());

		assertTrue(failure.getMessage().contains(PREFIX + "qr-base-url"));
		assertFalse(failure.getMessage().contains(PREFIX + "max-anticipation"));
	}

	@Test
	void shouldTreatBlankValuesAsMissing() {
		final Map<String, Object> properties = allParameters();
		properties.put(PREFIX + "qr-base-url", "   ");
		properties.put(PREFIX + "document.allowed-types", "");

		final IllegalStateException failure = assertThrows(IllegalStateException.class,
				() -> new SpringVisitParametersAdapter(environmentWith(properties)).get());

		assertTrue(failure.getMessage().contains(PREFIX + "qr-base-url"));
		assertTrue(failure.getMessage().contains(PREFIX + "document.allowed-types"));
	}

	private static Map<String, Object> allParameters() {
		final Map<String, Object> properties = new HashMap<>();
		properties.put(PREFIX + "max-authorization-duration", "24h");
		properties.put(PREFIX + "max-anticipation", "30d");
		properties.put(PREFIX + "start-tolerance", "10m");
		properties.put(PREFIX + "document.max-size", "5MB");
		properties.put(PREFIX + "document.allowed-types", "application/pdf,image/jpeg,image/png");
		properties.put(PREFIX + "qr-base-url", "https://smartlife.sarl/visitas/autorizaciones/");
		properties.put(PREFIX + "time-zone", "UTC");
		return properties;
	}

	private static StandardEnvironment environmentWith(final Map<String, Object> properties) {
		final StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
		return environment;
	}

}