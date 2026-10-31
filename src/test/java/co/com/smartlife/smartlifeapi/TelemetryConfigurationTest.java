package co.com.smartlife.smartlifeapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@SpringBootTest
class TelemetryConfigurationTest {

	private static final Path MAIN_PROPERTIES = Path.of("src", "main", "resources", "application.properties");

	@Autowired
	private ApplicationContext context;

	private static Properties loadMainProperties() throws IOException {
		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(MAIN_PROPERTIES)) {
			properties.load(input);
		}
		return properties;
	}

	@Test
	void otlpMetricsExportShouldBeOffInTheRunningApplication() {
		for (MeterRegistry registry : context.getBeansOfType(MeterRegistry.class).values()) {
			assertFalse(registry.getClass().getName().contains("Otlp"),
					"Hay un registro OTLP activo: " + registry.getClass().getName());
			if (registry instanceof CompositeMeterRegistry composite) {
				composite.getRegistries().forEach(inner -> assertFalse(inner.getClass().getName().contains("Otlp"),
						"Hay un registro OTLP activo: " + inner.getClass().getName()));
			}
		}
	}

	@Test
	void telemetryShouldBeOffByDefaultInTheMainProperties() throws IOException {
		Properties properties = loadMainProperties();

		assertEquals("${OTLP_METRICS_ENABLED:false}",
				properties.getProperty("management.otlp.metrics.export.enabled"));
		assertEquals("${OTLP_TRACING_ENABLED:false}",
				properties.getProperty("management.tracing.export.otlp.enabled"));
	}

	@Test
	void onlyHealthShouldBeExposedByDefaultInTheMainProperties() throws IOException {
		Properties properties = loadMainProperties();

		assertEquals("${MANAGEMENT_ENDPOINTS:health}",
				properties.getProperty("management.endpoints.web.exposure.include"));
	}

	@Test
	void theMainPropertiesShouldNotContainHardcodedCredentials() throws IOException {
		Properties properties = loadMainProperties();

		for (String name : properties.stringPropertyNames()) {
			String lowerName = name.toLowerCase();
			boolean sensitive = lowerName.contains("authorization") || lowerName.contains("password")
					|| lowerName.contains("token") || lowerName.contains("secret")
					|| lowerName.contains("api-key") || lowerName.contains("access-key")
					|| lowerName.contains("credentials");
			if (sensitive) {
				String value = properties.getProperty(name).trim();
				assertTrue(value.isEmpty() || value.startsWith("${"),
						"La propiedad " + name + " tiene un valor escrito en el archivo");
			}
		}
	}
}