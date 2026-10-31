package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.unit.DataSize;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.VisitParameters;

@SpringBootTest(classes = ParameterCatalogConfiguration.class, properties = {
		"smartlife.visits.max-authorization-duration=12h", "smartlife.visits.max-anticipation=7d",
		"smartlife.visits.start-tolerance=15m", "smartlife.visits.document.max-size=2MB",
		"smartlife.visits.document.allowed-types=application/pdf,image/png",
		"smartlife.visits.qr-base-url=https://smartlife.test/visitas/",
		"smartlife.visits.time-zone=America/Bogota" })
class ParameterCatalogConfigurationTest {

	@AfterEach
	void restoreTheDefaults() {
		ParameterCatalog.load(VisitParameters.defaults());
	}

	@Test
	void theParametersOfTheEnvironmentShouldReachTheCatalog() {
		VisitParameters visits = ParameterCatalog.visits();

		assertEquals(Duration.ofHours(12), visits.maxAuthorizationDuration());
		assertEquals(Duration.ofDays(7), visits.maxAnticipation());
		assertEquals(Duration.ofMinutes(15), visits.startTolerance());
		assertEquals(DataSize.ofMegabytes(2), visits.documentMaxSize());
		assertEquals(List.of("application/pdf", "image/png"), visits.documentAllowedTypes());
		assertEquals("https://smartlife.test/visitas/", visits.qrBaseUrl());
		assertEquals(ZoneId.of("America/Bogota"), visits.timeZone());
	}
}