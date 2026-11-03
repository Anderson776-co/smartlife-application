package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;

public final class VisitObservationIsValid implements Rule {

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

		if (TextHelper.getDefaultWithTrim((String) data[0]).length() > ParameterCatalog.OBSERVATION_MAX_LENGTH) {
			throw VisitRuleErrors.business("VISIT_OBSERVATION_TOO_LONG");
		}
	}

}
