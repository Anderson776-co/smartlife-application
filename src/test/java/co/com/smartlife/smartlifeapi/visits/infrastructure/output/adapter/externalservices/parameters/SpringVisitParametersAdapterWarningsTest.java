package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/** Comprueba que se avisa en el registro cuando un parametro no llega del servidor de configuracion. */
class SpringVisitParametersAdapterWarningsTest {

	private Logger logger;
	private ListAppender<ILoggingEvent> appender;

	@BeforeEach
	void attachAppender() {
		logger = (Logger) LoggerFactory.getLogger(SpringVisitParametersAdapter.class);
		appender = new ListAppender<>();
		appender.start();
		logger.addAppender(appender);
	}

	@AfterEach
	void detachAppender() {
		logger.detachAppender(appender);
	}

	private static StandardEnvironment environmentWith(final Map<String, Object> properties) {
		final StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
		return environment;
	}

	private List<String> warnings() {
		return appender.list.stream().filter(event -> event.getLevel() == Level.WARN)
				.map(ILoggingEvent::getFormattedMessage).toList();
	}

	@Test
	void shouldWarnOnceForEachParameterThatUsesTheDefault() {
		new SpringVisitParametersAdapter(environmentWith(Map.of())).get();

		final List<String> warnings = warnings();
		assertEquals(7, warnings.size());
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.max-authorization-duration")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.max-anticipation")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.start-tolerance")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.document.max-size")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.document.allowed-types")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.qr-base-url")));
		assertTrue(warnings.stream().anyMatch(message -> message.contains("smartlife.visits.time-zone")));
	}

	@Test
	void shouldNotWarnWhenEveryParameterComesFromTheConfigurationServer() {
		new SpringVisitParametersAdapter(environmentWith(Map.of(
				"smartlife.visits.max-authorization-duration", "24h",
				"smartlife.visits.max-anticipation", "30d",
				"smartlife.visits.start-tolerance", "10m",
				"smartlife.visits.document.max-size", "5MB",
				"smartlife.visits.document.allowed-types", "application/pdf,image/png",
				"smartlife.visits.qr-base-url", "https://smartlife.sarl/visitas/autorizaciones/",
				"smartlife.visits.time-zone", "UTC"))).get();

		assertEquals(List.of(), warnings());
	}

	@Test
	void shouldWarnOnlyAboutTheMissingParameter() {
		new SpringVisitParametersAdapter(environmentWith(Map.of(
				"smartlife.visits.max-authorization-duration", "24h",
				"smartlife.visits.max-anticipation", "30d",
				"smartlife.visits.start-tolerance", "10m",
				"smartlife.visits.document.max-size", "5MB",
				"smartlife.visits.document.allowed-types", "application/pdf",
				"smartlife.visits.time-zone", "UTC"))).get();

		final List<String> warnings = warnings();
		assertEquals(1, warnings.size());
		assertTrue(warnings.get(0).contains("smartlife.visits.qr-base-url"));
	}
}