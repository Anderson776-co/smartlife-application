package co.com.smartlife.smartlifeapi.users.service.usecase.contract;

import java.time.Duration;

import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.dto.AuthenticatedUserDTO;

public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {
	
	private final IdentityProviderOutputPort identityProvider;
	
	private static final Duration SESSION_DURATION =
            Duration.ofDays(5);
	
	

	public AuthenticateUserUseCaseImpl(IdentityProviderOutputPort identityProvider) {
		this.identityProvider = identityProvider;
	}



	@Override
	public AuthenticatedSessionDTO authenticate(String idToken) {
		AuthenticatedUserDTO user =
                identityProvider.verifyToken(idToken);

        String cookie = identityProvider.createSessionCookie(
                idToken,
                SESSION_DURATION);

        return new AuthenticatedSessionDTO(user, cookie);
    }

}
