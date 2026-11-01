package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.VisitParameters;

/**
 * Prueba manual contra un servidor de parametros REAL. Solo corre si se pasa la
 * propiedad configServerUrl (por ejemplo, -DconfigServerUrl=http://localhost:8888);
 * en el CI se omite. Las propiedades de prueba no traen los parametros de
 * visitas, asi que si estos llegan es porque los entrego el servidor.
 */
@EnabledIfSystemProperty(named = "configServerUrl", matches = ".+")
@SpringBootTest(classes = ParameterCatalogConfiguration.class, properties = {
		"spring.application.name=smartlife-api", "spring.cloud.config.enabled=true",
		"spring.cloud.config.import-check.enabled=true",
		"spring.config.import=optional:configserver:${configServerUrl}" })
class ConfigServerRealConnectionTest {

	@Autowired
	private Environment environment;

	@AfterEach
	void restoreTheDefaults() {
		ParameterCatalog.load(VisitParameters.defaults());
	}

	@Test
	void theParametersShouldComeFromTheRealConfigServer() {
		assertEquals("24h", environment.getProperty("smartlife.visits.max-authorization-duration"));
		assertEquals("30d", environment.getProperty("smartlife.visits.max-anticipation"));
		assertEquals("10m", environment.getProperty("smartlife.visits.start-tolerance"));
		assertEquals("5MB", environment.getProperty("smartlife.visits.document.max-size"));
		assertEquals("application/pdf,image/jpeg,image/png",
				environment.getProperty("smartlife.visits.document.allowed-types"));
		assertEquals("UTC", environment.getProperty("smartlife.visits.time-zone"));
		assertEquals(VisitParameters.defaults(), ParameterCatalog.visits());
	}
}