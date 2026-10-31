package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.dto.AuthenticatedUserDTO;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRoleRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;
import co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation.ValidateCreatedUser;
import co.com.smartlife.smartlifeapi.users.service.usecase.authvalidation.ValidateUserIsActive;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.ValidateSessionUseCase;

@Service
public class ValidateSessionImpl implements ValidateSessionUseCase{

	private final IdentityProviderOutputPort identityProvider;
	private final boolean checkRevoked;
	private final ValidateCreatedUser validateCreatedUser;
	private final ValidateUserIsActive validateUserIsActive;
	private final UserRoleRepository userRoleRepository;
	
	public ValidateSessionImpl(final IdentityProviderOutputPort identityProvider, @Value("${smartlife.session.check-revoked:true}") boolean checkRevoked,
			final ValidateCreatedUser validateCreatedUser, final ValidateUserIsActive validateUserIsActive, final UserRoleRepository userRoleRepository) {
		this.identityProvider = identityProvider;
		this.checkRevoked = checkRevoked;
		this.validateCreatedUser = validateCreatedUser;
		this.validateUserIsActive = validateUserIsActive;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	public CurrentUser execute(String input) {
		final String sessionCookie = input;
		final AuthenticatedUserDTO identity = identityProvider.verifySessionCookie(sessionCookie, checkRevoked);
		
		final UserEntity user = validateCreatedUser.findCreatedUser(identity.uid());
		validateUserIsActive.validate(user.getIsActive());
		
		final Set<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
	            .map(userRole -> userRole.getRole().getName())   // ADMIN, OWNER, RESIDENT
	            .collect(Collectors.toUnmodifiableSet());
		
		return new CurrentUser(user.getId(), user.getEmail(), roles, urbanizationIdOf(user));
	}
	
	private static UUID urbanizationIdOf(final UserEntity user) {
		return ObjectHelper.isNull(user.getUrbanization())
				? UUIDHelper.getUUIDHelper().getDefault()
				: user.getUrbanization().getId();
	}

}
