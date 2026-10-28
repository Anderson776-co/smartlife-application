package co.com.smartlife.smartlifeapi.crosscutting.rules.generics;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsUUIDPresentSpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;

public final class IsUUIDPresentRule implements Rule {
	
	private static final Rule instance = new IsUUIDPresentRule();
	
	private IsUUIDPresentRule() {
		
	}
	
	public static void executeRule(final Object...data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if(!new IsMandatorySpecification().isSatisfiedBy(data)) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage = MessageCatalogProvider.getContent("NULL_PARAMETERS_IS_UUID_PRESENT_RULE");
            throw SmartLifeException.create(userMessage, technicalMessage);
	    }
	
	    if (data.length < 1) {
	    		var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
	    		var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_IS_UUID_PRESENT_RULE");
	    		throw SmartLifeException.create(userMessage, technicalMessage);
		}
	    
	    var uuidData = (UUID) data [0];
	    
	    var isUUIDPresentSpecification = new IsUUIDPresentSpecification();
	    
	    if(!isUUIDPresentSpecification.isSatisfiedBy(uuidData)) {
	    	var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
	    	var technicalMessage = MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_UUID_IS_NOT_PRESENT");;
	    	throw SmartLifeException.create(userMessage, technicalMessage);
	    }
	}
}

