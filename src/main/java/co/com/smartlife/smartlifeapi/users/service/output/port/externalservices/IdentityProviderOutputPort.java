package co.com.smartlife.smartlifeapi.users.service.output.port.externalservices;

import java.time.Duration;

import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.dto.AuthenticatedUserDTO;

public interface IdentityProviderOutputPort {

	AuthenticatedUserDTO verifyToken(String idToken);
	String createSessionCookie(String idToken, Duration expiration);
	AuthenticatedUserDTO verifySessionCookie(String sessionCookie, boolean checkRevoked);
	void revokeSession(String uid);
}