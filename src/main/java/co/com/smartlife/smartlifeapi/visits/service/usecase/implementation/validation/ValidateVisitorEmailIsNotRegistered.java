package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.VisitRuleErrors;

public final class ValidateVisitorEmailIsNotRegistered implements Validator {

	private static final Validator instance = new ValidateVisitorEmailIsNotRegistered();

	private ValidateVisitorEmailIsNotRegistered() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		if (data.length < 2) {
			throw VisitRuleErrors.incompleteParameters("VISITOR_EMAIL_IS_NOT_REGISTERED");
		}

		final var email = (String) data[0];
		final var visitorRepository = (VisitorRepository) data[1];

		if (visitorRepository.findByEmail(email).isPresent()) {
			throw VisitRuleErrors.business("VISITOR_EMAIL_ALREADY_REGISTERED");
		}
	}
}
