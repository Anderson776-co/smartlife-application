package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

class SpringVisitParametersAdapterTest {

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
	void shouldFallBackToDefaultsWhenNothingIsConfigured() {
		final VisitParametersDTO parameters = new SpringVisitParametersAdapter(environmentWith(Map.of())).get();

		assertEquals(Duration.ofHours(24), parameters.maxAuthorizationDuration());
		assertEquals(Duration.ofDays(30), parameters.maxAnticipation());
		assertEquals(Duration.ofMinutes(10), parameters.startTolerance());
		assertEquals(5L * 1024 * 1024, parameters.documentMaxSizeInBytes());
		assertEquals(Set.of("application/pdf", "image/jpeg", "image/png"), parameters.documentAllowedTypes());
		assertEquals(ZoneId.of("UTC"), parameters.timeZone());
	}

	@Test
	void shouldAddTheTrailingSlashToTheQrBaseUrl() {
		final VisitParametersDTO parameters = new SpringVisitParametersAdapter(
				environmentWith(Map.of("smartlife.visits.qr-base-url", "http://host/visitas"))).get();
		final UUID id = UUID.randomUUID();

		assertEquals("http://host/visitas/" + id, parameters.qrUrlFor(id));
	}

	@Test
	void shouldReflectPropertyChangesOnTheNextCall() {
		final StandardEnvironment environment = environmentWith(Map.of("smartlife.visits.max-anticipation", "7d"));
		final SpringVisitParametersAdapter adapter = new SpringVisitParametersAdapter(environment);
		assertEquals(Duration.ofDays(7), adapter.get().maxAnticipation());

		environment.getPropertySources().addFirst(
				new MapPropertySource("refreshed", Map.of("smartlife.visits.max-anticipation", "3d")));

		assertEquals(Duration.ofDays(3), adapter.get().maxAnticipation());
	}

	private static StandardEnvironment environmentWith(final Map<String, Object> properties) {
		final StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
		return environment;
	}

}
