package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.domain.validator.Validator;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;

@Component
public final class ValidateVisitReason implements Validator{

	@Override
	public void validate(Object... data) {
		
		if (!new IsMandatorySpecification().isSatisfiedBy(data)) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
			var technicalMessage = MessageCatalogProvider.getContent("NULL_PARAMETERS_VALIDATE_VISIT_REASON");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		if (data.length < 1) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");

            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
	}

}
