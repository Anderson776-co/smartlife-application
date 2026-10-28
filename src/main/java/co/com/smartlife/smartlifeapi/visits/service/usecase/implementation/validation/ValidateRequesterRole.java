package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;

@Component
public final class ValidateRequesterRole implements Validator{
	
	public static final String OWNER = "OWNER";
	public static final String RESIDENT = "RESIDENT";

	@Override
	public void validate(Object... data) {
		
		if (!new IsMandatorySpecification().isSatisfiedBy(data) || data.length < 1) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage =MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE");
            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
		var currentUser = (CurrentUser) data[0];
		
		validateRole(currentUser);
	}
	
	private void validateRole(final CurrentUser requester) {
		if (!requester.hasAnyRole(OWNER, RESIDENT)) {
			var userMessage = MessageCatalogProvider.getContent("USER_ROLE_NOT_ALLOWED");
			var technicalMessage = MessageCatalogProvider.getContent("PENDIENTE");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
	}

}
