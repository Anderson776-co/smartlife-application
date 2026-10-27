package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.domain.validator.Validator;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;

@Component
public final class ValidateRequesterRole implements Validator{
	
	public static final String OWNER = "OWNER";
	public static final String RESIDENT = "RESIDENT";

	@Override
	public void validate(Object... data) {
		final CurrentUser requester = (CurrentUser) data[0];
		
		if (! new IsMandatorySpecification().isSatisfiedBy(requester) || !requester.hasAnyRole(OWNER, RESIDENT)) {
			var userMessage = MessageCatalogProvider.getContent("REQUEST_NOT_ALLOWED");
			var technicalMessage = MessageCatalogProvider.getContent("VISIT_AUTHORIZATION_ROLE_NOT_ALLOWED");
			throw SmartLifeException.createAuthorization(userMessage, technicalMessage);
		}
		
	}

}
