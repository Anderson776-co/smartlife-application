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
		final Instant twoHoursAhead = NOW.plus(Duration.ofHours(2));
		final Instant oneHourAhead = NOW.plus(Duration.ofHours(1));
		assertThrows(SmartLifeException.class, () -> run(Instant.EPOCH, twoHoursAhead));
		assertThrows(SmartLifeException.class, () -> run(oneHourAhead, Instant.EPOCH));
	}

	@Test
	void shouldRejectAnEndThatIsNotAfterTheStart() {
		final Instant start = NOW.plus(Duration.ofHours(2));
		assertThrows(SmartLifeException.class, () -> run(start, start));
		final Instant oneMinuteBefore = start.minus(Duration.ofMinutes(1));
		assertThrows(SmartLifeException.class, () -> run(start, oneMinuteBefore));
	}

	@Test
	void shouldRejectAStartBeyondTheTolerance() {
		final Instant tooEarly = NOW.minus(Duration.ofMinutes(11));
		final Instant end = NOW.plus(Duration.ofHours(2));
		assertThrows(SmartLifeException.class, () -> run(tooEarly, end));
	}

	@Test
	void shouldRejectARangeThatAlreadyEnded() {
		final Instant start = NOW.minus(Duration.ofMinutes(8));
		final Instant end = NOW.minus(Duration.ofMinutes(1));
		assertThrows(SmartLifeException.class, () -> run(start, end));
	}

	@Test
	void shouldRejectAStartTooFarInTheFuture() {
		final Instant start = NOW.plus(Duration.ofDays(30)).plusSeconds(1);
		final Instant end = start.plus(Duration.ofHours(2));
		assertThrows(SmartLifeException.class, () -> run(start, end));
	}

	@Test
	void shouldRejectADurationOverTheMaximum() {
		final Instant start = NOW.plus(Duration.ofHours(1));
		final Instant end = start.plus(Duration.ofHours(24)).plusSeconds(1);
		assertThrows(SmartLifeException.class, () -> run(start, end));
	}

	@Test
	void shouldRejectIncompleteData() {
		assertThrows(SmartLifeException.class, () -> VisitDatesAreValid.executeRule(NOW));
	}

	private static void run(final Instant start, final Instant end) {
		VisitDatesAreValid.executeRule(start, end, NOW, PARAMETERS);
	}

}
