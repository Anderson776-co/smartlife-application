package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.VisitObservationIsValid;

public final class ValidateVisitObservation implements Validator {

	private static final Validator instance = new ValidateVisitObservation();

	private ValidateVisitObservation() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		VisitObservationIsValid.executeRule(data);
	}

}
