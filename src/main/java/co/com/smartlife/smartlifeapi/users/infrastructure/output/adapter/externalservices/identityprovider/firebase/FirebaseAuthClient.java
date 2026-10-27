package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;
import java.time.Duration;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.VerifiedToken;

interface FirebaseAuthClient {
	VerifiedToken verifyIdToken(String idToken, boolean checkRevoked);

	String createSessionCookie(String idToken, Duration expiration);

	VerifiedToken verifySessionCookie(String sessionCookie, boolean checkRevoked);

	void revokeRefreshTokens(String uid);

}
