package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.VisitDatesAreValid;

public final class ValidateVisitDates implements Validator {

	private static final Validator instance = new ValidateVisitDates();

	private ValidateVisitDates() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		VisitDatesAreValid.executeRule(data);
	}

}
