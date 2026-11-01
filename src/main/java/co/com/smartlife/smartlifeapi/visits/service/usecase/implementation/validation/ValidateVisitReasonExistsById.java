package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.VisitReasonExistsById;

public class ValidateVisitReasonExistsById implements Validator{

	private static Validator instance = new ValidateVisitReasonExistsById();
	
	public static void executeValidation(Object... data) {
		instance.validate(data);
	}
	
	@Override
	public void validate(Object... data) {
		VisitReasonExistsById.executeRule(data);
	}

}
