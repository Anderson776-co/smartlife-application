package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;

public class ValidateAuthorizationData implements Validator{
	
	//Implementar con catalogo de parametros
	
	@Override
	public void validate(Object... data) {
		if (!new IsMandatorySpecification().isSatisfiedBy(data) || data.length < 4) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage =MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE");
            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
	}

}
