package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.input.dto.response.SessionDto;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.dto.AuthenticatedUserDTO;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;
import co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation.ValidateCreatedUser;
import co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation.ValidateUserIsActive;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.LoginUseCase;

@Service
public class LoginUseCaseImpl implements LoginUseCase{
	
	private final IdentityProviderOutputPort identityProvider;
	private final Duration sessionDuration;
	private final ValidateCreatedUser validateCreatedUser;
	private final ValidateUserIsActive validateUserIsActive;
	
	

	public LoginUseCaseImpl(final IdentityProviderOutputPort identityProvider, @Value("${smartlife.session.duration:5d}") final Duration sessionDuration,
			final ValidateCreatedUser validateCreatedUser, final ValidateUserIsActive validateUserIsActive) {
		this.identityProvider = identityProvider;
		this.sessionDuration = sessionDuration;
		this.validateCreatedUser = validateCreatedUser;
		this.validateUserIsActive = validateUserIsActive;
	}

	@Override
	public SessionDto execute(String input) {
		
		final String tokenId = input;
		final AuthenticatedUserDTO identity = identityProvider.verifyToken(tokenId);
		
		final UserEntity user= validateCreatedUser.findCreatedUser(identity.uid());
		validateUserIsActive.validate(user.getIsActive());
		
		final String cookie = identityProvider.createSessionCookie(tokenId, sessionDuration);
		return new SessionDto(cookie, sessionDuration);
	}

}
