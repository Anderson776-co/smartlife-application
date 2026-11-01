package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.PropertyVisitExistsById;

public class ValidatePropertyVisitExistsById implements Validator{

	private static final Validator instance = new ValidatePropertyVisitExistsById();
	
	public static void executeValidation(Object... data) {
		instance.validate(data);
	}
	
	@Override
	public void validate(Object... data) {
		PropertyVisitExistsById.executeRule(data);
	}
	

}
