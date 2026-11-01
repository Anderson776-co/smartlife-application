package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;

public class UserAuthorizatorIsActive implements Rule{
	
	private static final Rule instance = new UserAuthorizatorIsActive();
	
	private UserAuthorizatorIsActive() {
	}
	
	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(Object... data) {
		
		if(data.length < 2) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
			var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_USER_AUTHORIZATOR_IS_ACTIVE_RULE");
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		var id = (UUID) data[0];
		var userAuthorizatorRepository = (AuthorizatorRepository) data[1];
		
		var isActive = userAuthorizatorRepository.findById(id).map(AuthorizatorEntity::getIsActive).orElse(Boolean.FALSE);
		
		if (!Boolean.TRUE.equals(isActive)){
			var userMessage = MessageCatalogProvider.getContent("USER_MESSAGE_USER_AUTHORIZATOR_ISNT_ACTIVE");
			var technicalMessage = MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_USER_AUTHORIZATOR_ISNT_ACTIVE");
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
	}

}
