package co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider;

import java.time.Duration;

import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.dto.AuthenticatedUserDTO;

public interface IdentityProviderOutputPort {

	AuthenticatedUserDTO verifyToken(String idToken);
	String createSessionCookie(String idToken, Duration expiration);
	AuthenticatedUserDTO verifySessionCookie(String sessionCookie, Boolean checkRevoked);
	void revokeSession(String uid);
}