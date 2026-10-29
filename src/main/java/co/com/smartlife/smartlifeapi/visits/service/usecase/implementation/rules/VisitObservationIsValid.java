package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;

/** La observacion es opcional y cabe en la columna {@code observation} (500). Dato: la observacion. */
public final class VisitObservationIsValid implements Rule {

	public static final int MAX_LENGTH = 500;

	private static final Rule instance = new VisitObservationIsValid();

	private VisitObservationIsValid() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 1) {
			throw VisitRuleErrors.incompleteParameters("VISIT_OBSERVATION_IS_VALID");
		}

		if (TextHelper.getDefaultWithTrim((String) data[0]).length() > MAX_LENGTH) {
			throw VisitRuleErrors.business("VISIT_OBSERVATION_TOO_LONG");
		}
	}

}
