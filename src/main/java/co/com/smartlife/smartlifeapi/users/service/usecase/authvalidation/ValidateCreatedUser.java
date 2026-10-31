package co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

@Component
public final class ValidateCreatedUser implements Validator{
	
	private final UserRepository userRepository;
	
	public ValidateCreatedUser(final UserRepository userRepsoitory) {
		this.userRepository = userRepsoitory;
	}

	@Override
	public void validate(Object... data) {
		
		final String identityProviderUid = (String) data [0];
		findCreatedUser(identityProviderUid);	
	}
	
	public UserEntity findCreatedUser(final String identityProviderUid) {
		 return userRepository.findByIdentityProviderUid(identityProviderUid)
		            .orElseThrow(() -> {
		                var userMessage =
		                    MessageCatalogProvider.getContent("USER_NOT_REGISTERED");

		                var technicalMessage =
		                    MessageCatalogProvider.getContent(
		                        "USER_NOT_REGISTRED_TRY_TO_LOG_IN");

		                return SmartLifeException.createAuthentication(
		                    userMessage, technicalMessage);
		            });
	}

}