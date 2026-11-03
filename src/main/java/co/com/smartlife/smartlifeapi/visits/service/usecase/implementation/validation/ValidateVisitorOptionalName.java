package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsStringPresentSpecification;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextFormatIsValid;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextLengthIsValid;

public final class ValidateVisitorOptionalName implements Validator {

	private static final Validator instance = new ValidateVisitorOptionalName();

	private ValidateVisitorOptionalName() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final String name = (String) data[0];
		final String fieldCode = (String) data[1];
		final String pattern = (String) data[2];
		final Integer minLength = (Integer) data[3];
		final Integer maxLength = (Integer) data[4];

		if (new IsStringPresentSpecification().isSatisfiedBy(name)) {
			TextFormatIsValid.executeRule(name, pattern, fieldCode);
			TextLengthIsValid.executeRule(name, minLength, maxLength, fieldCode);
		}
	}

}
