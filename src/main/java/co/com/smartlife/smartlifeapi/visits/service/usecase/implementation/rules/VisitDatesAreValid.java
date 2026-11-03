package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.time.Duration;
import java.time.Instant;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

public final class VisitDatesAreValid implements Rule {

	private static final Rule instance = new VisitDatesAreValid();

	private VisitDatesAreValid() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 4) {
			throw VisitRuleErrors.incompleteParameters("VISIT_DATES_ARE_VALID");
		}

		final var start = (Instant) data[0];
		final var end = (Instant) data[1];
		final var now = (Instant) data[2];
		final var parameters = (VisitParametersDTO) data[3];

		if (isMissing(start) || isMissing(end)) {
			throw VisitRuleErrors.business("VISIT_DATES_REQUIRED");
		}
		if (!end.isAfter(start)) {
			throw VisitRuleErrors.business("VISIT_DATES_INVALID_RANGE");
		}
		if (start.isBefore(now.minus(parameters.startTolerance())) || !end.isAfter(now)) {
			throw VisitRuleErrors.business("VISIT_DATES_IN_THE_PAST");
		}
		if (start.isAfter(now.plus(parameters.maxAnticipation()))) {
			throw VisitRuleErrors.business("VISIT_START_TOO_FAR_IN_THE_FUTURE");
		}
		if (Duration.between(start, end).compareTo(parameters.maxAuthorizationDuration()) > 0) {
			throw VisitRuleErrors.business("VISIT_DURATION_EXCEEDED");
		}
	}

	private static boolean isMissing(final Instant date) {
		return date == null || Instant.EPOCH.equals(date);
	}

}
