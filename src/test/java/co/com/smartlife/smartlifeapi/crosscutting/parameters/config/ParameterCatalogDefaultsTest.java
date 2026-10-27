package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.VisitParameters;

@SpringBootTest(classes = ParameterCatalogConfiguration.class)
class ParameterCatalogDefaultsTest {

	@AfterEach
	void restoreTheDefaults() {
		ParameterCatalog.load(VisitParameters.defaults());
	}

	@Test
	void withoutConfigurationTheFallbackValuesShouldBeLoaded() {
		assertEquals(VisitParameters.defaults(), ParameterCatalog.visits());
	}
}