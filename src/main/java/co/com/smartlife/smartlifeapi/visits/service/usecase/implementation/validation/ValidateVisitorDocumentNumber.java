package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextFormatIsValid;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextIsPresent;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextLengthIsValid;

public final class ValidateVisitorDocumentNumber implements Validator {

	static final String FIELD = "VISITOR_DOCUMENT";

	private static final Validator instance = new ValidateVisitorDocumentNumber();

	private ValidateVisitorDocumentNumber() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final String documentNumber = data.length > 0 ? (String) data[0] : null;

		TextIsPresent.executeRule(documentNumber, FIELD);
		TextFormatIsValid.executeRule(documentNumber, ParameterCatalog.DOCUMENT_NUMBER_PATTERN, FIELD);
		TextLengthIsValid.executeRule(documentNumber, ParameterCatalog.DOCUMENT_NUMBER_MIN_LENGTH,
				ParameterCatalog.DOCUMENT_NUMBER_MAX_LENGTH, FIELD);
	}

}
