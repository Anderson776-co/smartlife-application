package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

class ParameterCatalogTest {

	@AfterEach
	void restoreTheDefaults() {
		ParameterCatalog.load(VisitParameters.defaults());
	}

	@Test
	void loadShouldReplaceTheVisitParameters() {
		VisitParameters other = new VisitParameters(Duration.ofHours(12), Duration.ofDays(7), Duration.ofMinutes(15),
				DataSize.ofMegabytes(2), List.of("application/pdf"), "https://smartlife.test/visitas/",
				ZoneId.of("America/Bogota"));

		ParameterCatalog.load(other);

		assertSame(other, ParameterCatalog.visits());
		assertEquals(Duration.ofMinutes(15), ParameterCatalog.visits().startTolerance());
	}

	@Test
	void loadShouldRejectNull() {
		assertThrows(NullPointerException.class, () -> ParameterCatalog.load(null));
	}

	@Test
	void visitsShouldNeverBeNull() {
		assertNotNull(ParameterCatalog.visits());
	}

	@Test
	void theConstructorShouldBePrivateAndInstantiableByReflection() throws Exception {
		Constructor<ParameterCatalog> constructor = ParameterCatalog.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		assertInstanceOf(ParameterCatalog.class, constructor.newInstance());
	}

	@Test
	void theConstructorShouldNotBePublic() {
		assertThrows(NoSuchMethodException.class, () -> ParameterCatalog.class.getConstructor());
	}
}