package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

class VisitParametersTest {

	private static final Duration HOUR = Duration.ofHours(1);
	private static final List<String> TYPES = List.of("application/pdf");
	private static final String URL = "https://smartlife.test/visitas/";
	private static final ZoneId UTC = ZoneId.of("UTC");

	@Test
	void defaultsShouldBeTheFallbackValuesOfApplicationProperties() {
		VisitParameters defaults = VisitParameters.defaults();

		assertEquals(Duration.ofHours(24), defaults.maxAuthorizationDuration());
		assertEquals(Duration.ofDays(30), defaults.maxAnticipation());
		assertEquals(Duration.ofMinutes(10), defaults.startTolerance());
		assertEquals(DataSize.ofMegabytes(5), defaults.documentMaxSize());
		assertEquals(List.of("application/pdf", "image/jpeg", "image/png"), defaults.documentAllowedTypes());
		assertEquals("http://localhost:5173/visitas/autorizaciones/", defaults.qrBaseUrl());
		assertEquals(ZoneId.of("UTC"), defaults.timeZone());
	}

	@Test
	void shouldKeepAnImmutableCopyOfTheAllowedTypes() {
		List<String> types = new ArrayList<>(TYPES);
		VisitParameters parameters = new VisitParameters(HOUR, HOUR, HOUR, DataSize.ofMegabytes(1), types, URL, UTC);

		types.add("image/png");

		assertEquals(TYPES, parameters.documentAllowedTypes());
		assertThrows(UnsupportedOperationException.class, () -> parameters.documentAllowedTypes().add("x"));
	}

	@Test
	void shouldRejectDurationsThatAreNotPositive() {
		DataSize size = DataSize.ofMegabytes(1);

		assertThrows(IllegalArgumentException.class,
				() -> new VisitParameters(Duration.ZERO, HOUR, HOUR, size, TYPES, URL, UTC));
		assertThrows(IllegalArgumentException.class,
				() -> new VisitParameters(HOUR, Duration.ofMinutes(-5), HOUR, size, TYPES, URL, UTC));
		assertThrows(IllegalArgumentException.class,
				() -> new VisitParameters(HOUR, HOUR, Duration.ZERO, size, TYPES, URL, UTC));
	}

	@Test
	void shouldRejectMissingValues() {
		DataSize size = DataSize.ofMegabytes(1);

		assertThrows(NullPointerException.class, () -> new VisitParameters(null, HOUR, HOUR, size, TYPES, URL, UTC));
		assertThrows(NullPointerException.class, () -> new VisitParameters(HOUR, HOUR, HOUR, null, TYPES, URL, UTC));
		assertThrows(NullPointerException.class, () -> new VisitParameters(HOUR, HOUR, HOUR, size, null, URL, UTC));
		assertThrows(NullPointerException.class, () -> new VisitParameters(HOUR, HOUR, HOUR, size, TYPES, URL, null));
	}

	@Test
	void shouldRejectAnEmptySizeAnEmptyListAndABlankUrl() {
		DataSize size = DataSize.ofMegabytes(1);

		assertThrows(IllegalArgumentException.class,
				() -> new VisitParameters(HOUR, HOUR, HOUR, DataSize.ofBytes(0), TYPES, URL, UTC));
		assertThrows(IllegalArgumentException.class,
				() -> new VisitParameters(HOUR, HOUR, HOUR, size, List.of(), URL, UTC));
		assertThrows(IllegalArgumentException.class, () -> new VisitParameters(HOUR, HOUR, HOUR, size, TYPES, " ", UTC));
		assertThrows(IllegalArgumentException.class, () -> new VisitParameters(HOUR, HOUR, HOUR, size, TYPES, null, UTC));
		assertTrue(VisitParameters.defaults().documentMaxSize().toBytes() > 0);
	}
}