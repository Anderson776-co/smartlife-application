package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.UserExistsById;

public class ValidateUserAuthorizatorExistsById implements Validator{

	private static final Validator instance = new ValidateUserAuthorizatorExistsById();
	
	public static void executeValidation(Object... data) {
		instance.validate(data);
	}
	
	@Override
	public void validate(Object... data) {
		UserExistsById.executeRule(data);	
	}
}
