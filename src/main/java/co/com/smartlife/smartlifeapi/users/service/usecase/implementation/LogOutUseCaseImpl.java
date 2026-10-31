package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.LogOutUseCase;

@Service
public class LogOutUseCaseImpl implements LogOutUseCase{

	private final UserRepository userRepository;
	private final IdentityProviderOutputPort identityProvider;
	
	public LogOutUseCaseImpl(final UserRepository userRepository,
			final IdentityProviderOutputPort identityProvider) {
		this.userRepository = userRepository;
		this.identityProvider = identityProvider;
	}



	@Override
	public void execute(UUID input) {
		
		final UUID userId = input;
		
		userRepository.findById(userId)
        .map(UserEntity::getExternalId)
        .ifPresent(identityProvider::revokeSession);
		
	}

}
