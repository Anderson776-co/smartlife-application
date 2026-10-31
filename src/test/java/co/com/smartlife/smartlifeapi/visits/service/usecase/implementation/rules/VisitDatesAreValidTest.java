package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

class VisitDatesAreValidTest {

	private static final Instant NOW = Instant.parse("2026-10-29T15:00:00Z");
	private static final VisitParametersDTO PARAMETERS = new VisitParametersDTO(Duration.ofHours(24),
			Duration.ofDays(30), Duration.ofMinutes(10), 1024, Set.of("application/pdf"), "http://qr/",
			ZoneOffset.UTC);

	@Test
	void shouldAcceptARangeInsideTheLimits() {
		assertDoesNotThrow(() -> run(NOW.plus(Duration.ofHours(1)), NOW.plus(Duration.ofHours(5))));
	}

	@Test
	void shouldAcceptAStartInsideTheTolerance() {
		assertDoesNotThrow(() -> run(NOW.minus(Duration.ofMinutes(5)), NOW.plus(Duration.ofHours(2))));
	}

	@Test
	void shouldAcceptTheMaximumDurationAndTheMaximumAnticipation() {
		final Instant start = NOW.plus(Duration.ofDays(30));
		assertDoesNotThrow(() -> run(start, start.plus(Duration.ofHours(24))));
	}

	@Test
	void shouldRejectMissingDates() {
		assertThrows(SmartLifeException.class, () -> run(Instant.EPOCH, NOW.plus(Duration.ofHours(2))));
		assertThrows(SmartLifeException.class, () -> run(NOW.plus(Duration.ofHours(1)), Instant.EPOCH));
	}

	@Test
	void shouldRejectAnEndThatIsNotAfterTheStart() {
		final Instant start = NOW.plus(Duration.ofHours(2));
		assertThrows(SmartLifeException.class, () -> run(start, start));
		assertThrows(SmartLifeException.class, () -> run(start, start.minus(Duration.ofMinutes(1))));
	}

	@Test
	void shouldRejectAStartBeyondTheTolerance() {
		assertThrows(SmartLifeException.class,
				() -> run(NOW.minus(Duration.ofMinutes(11)), NOW.plus(Duration.ofHours(2))));
	}

	@Test
	void shouldRejectARangeThatAlreadyEnded() {
		assertThrows(SmartLifeException.class,
				() -> run(NOW.minus(Duration.ofMinutes(8)), NOW.minus(Duration.ofMinutes(1))));
	}

	@Test
	void shouldRejectAStartTooFarInTheFuture() {
		final Instant start = NOW.plus(Duration.ofDays(30)).plusSeconds(1);
		assertThrows(SmartLifeException.class, () -> run(start, start.plus(Duration.ofHours(2))));
	}

	@Test
	void shouldRejectADurationOverTheMaximum() {
		final Instant start = NOW.plus(Duration.ofHours(1));
		assertThrows(SmartLifeException.class, () -> run(start, start.plus(Duration.ofHours(24)).plusSeconds(1)));
	}

	@Test
	void shouldRejectIncompleteData() {
		assertThrows(SmartLifeException.class, () -> VisitDatesAreValid.executeRule(NOW));
	}

	private static void run(final Instant start, final Instant end) {
		VisitDatesAreValid.executeRule(start, end, NOW, PARAMETERS);
	}

}
