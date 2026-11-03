package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextFormatIsValid;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextIsPresent;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextLengthIsValid;

public final class ValidateVisitorEmail implements Validator {

	static final String FIELD = "VISITOR_EMAIL";

	private static final Validator instance = new ValidateVisitorEmail();

	private ValidateVisitorEmail() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final String email = (String) data[0];

		TextIsPresent.executeRule(email, FIELD);
		TextFormatIsValid.executeRule(email, ParameterCatalog.EMAIL_PATTERN, FIELD);
		TextLengthIsValid.executeRule(email, ParameterCatalog.EMAIL_MIN_LENGTH, ParameterCatalog.EMAIL_MAX_LENGTH,
				FIELD);
	}

}
