package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

/**
 * Prueba manual contra un servidor de parametros REAL. Solo corre si se pasa la propiedad configServerUrl
 * (por ejemplo, -DconfigServerUrl=http://localhost:8888); en el CI se omite. Las propiedades de prueba no
 * traen los parametros de visitas, y el adaptador no tiene valores por defecto: si esta prueba pasa, es
 * porque el servidor entrego los siete parametros.
 */
@EnabledIfSystemProperty(named = "configServerUrl", matches = ".+")
@SpringBootTest(classes = SpringVisitParametersAdapter.class, properties = {
		"spring.application.name=smartlife-api", "spring.cloud.config.enabled=true",
		"spring.cloud.config.import-check.enabled=true",
		"spring.config.import=optional:configserver:${configServerUrl}" })
class SpringVisitParametersAdapterRealConnectionTest {

	@Autowired
	private SpringVisitParametersAdapter adapter;

	@Test
	void theParametersShouldComeFromTheRealConfigServer() {
		final VisitParametersDTO parameters = adapter.get();

		assertNotNull(parameters.maxAuthorizationDuration());
		assertNotNull(parameters.maxAnticipation());
		assertNotNull(parameters.startTolerance());
		assertTrue(parameters.documentMaxSizeInBytes() > 0);
		assertTrue(parameters.documentAllowedTypes().contains("application/pdf"));
		assertTrue(parameters.qrBaseUrl().startsWith("https://"), "El QR apunta a: " + parameters.qrBaseUrl());
	}

}