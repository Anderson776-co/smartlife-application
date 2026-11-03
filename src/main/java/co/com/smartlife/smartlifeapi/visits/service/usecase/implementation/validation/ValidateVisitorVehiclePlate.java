package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsStringPresentSpecification;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextFormatIsValid;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextLengthIsValid;

public final class ValidateVisitorVehiclePlate implements Validator {

	static final String FIELD = "VISITOR_PLATE";

	private static final Validator instance = new ValidateVisitorVehiclePlate();

	private ValidateVisitorVehiclePlate() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final String vehiclePlate = (String) data[0];

		if (new IsStringPresentSpecification().isSatisfiedBy(vehiclePlate)) {
			TextFormatIsValid.executeRule(vehiclePlate, ParameterCatalog.VEHICLE_PLATE_PATTERN, FIELD);
			TextLengthIsValid.executeRule(vehiclePlate, ParameterCatalog.VEHICLE_PLATE_MIN_LENGTH,
					ParameterCatalog.VEHICLE_PLATE_MAX_LENGTH, FIELD);
		}
	}

}
