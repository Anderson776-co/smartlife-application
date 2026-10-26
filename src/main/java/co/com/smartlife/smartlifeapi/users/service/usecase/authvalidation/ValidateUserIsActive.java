package co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.domain.validator.Validator;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.MessageCatalogProvider;

@Component
public final class ValidateUserIsActive implements Validator{

	@Override
	public void validate(Object... data) {
		
		final Boolean userIsActive = (Boolean) data [0];
		
		if (!Boolean.TRUE.equals(userIsActive)) {
			var userMessage = MessageCatalogProvider.getContent("USER_INACTIVE");
			var technicalMessage = MessageCatalogProvider.getContent("USER_INACTIVE_TRIES_TO_LOG_IN");
			
			throw SmartLifeException.createAuthentication(userMessage, technicalMessage);
		}
		
	}

}
