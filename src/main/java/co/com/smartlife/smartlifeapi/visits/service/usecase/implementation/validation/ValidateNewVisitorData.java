package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;


public final class ValidateNewVisitorData implements Validator {

	private static final String FIRST_NAME = "VISITOR_FIRST_NAME";
	private static final String LAST_NAME = "VISITOR_LAST_NAME";
	private static final String MIDDLE_NAME = "VISITOR_MIDDLE_NAME";
	private static final String SECOND_LAST_NAME = "VISITOR_SECOND_LAST_NAME";

	private static final Validator instance = new ValidateNewVisitorData();

	private ValidateNewVisitorData() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final VisitorDomain visitor = (VisitorDomain) data[0];

		ValidateVisitorRequiredName.executeValidation(visitor.firstName(), FIRST_NAME,
				ParameterCatalog.FIRST_NAME_PATTERN, ParameterCatalog.FIRST_NAME_MIN_LENGTH,
				ParameterCatalog.FIRST_NAME_MAX_LENGTH);
		ValidateVisitorRequiredName.executeValidation(visitor.lastName(), LAST_NAME,
				ParameterCatalog.LAST_NAME_PATTERN, ParameterCatalog.LAST_NAME_MIN_LENGTH,
				ParameterCatalog.LAST_NAME_MAX_LENGTH);
		ValidateVisitorOptionalName.executeValidation(visitor.middleName(), MIDDLE_NAME,
				ParameterCatalog.MIDDLE_NAME_PATTERN, ParameterCatalog.MIDDLE_NAME_MIN_LENGTH,
				ParameterCatalog.MIDDLE_NAME_MAX_LENGTH);
		ValidateVisitorOptionalName.executeValidation(visitor.secondLastName(), SECOND_LAST_NAME,
				ParameterCatalog.SECOND_LAST_NAME_PATTERN, ParameterCatalog.SECOND_LAST_NAME_MIN_LENGTH,
				ParameterCatalog.SECOND_LAST_NAME_MAX_LENGTH);
		ValidateVisitorEmail.executeValidation(visitor.email());
		ValidateVisitorVehiclePlate.executeValidation(visitor.vehiclePlate());
	}

}
