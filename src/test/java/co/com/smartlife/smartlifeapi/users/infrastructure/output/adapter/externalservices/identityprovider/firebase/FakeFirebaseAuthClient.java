package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

import java.time.Duration;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.VerifiedToken;

final class FakeFirebaseAuthClient implements FirebaseAuthClient{

	
	VerifiedToken idToken;
	VerifiedToken sessionToken;
	boolean rejectAll;
	String createdCookie = "cookie-de-sesion";

	Duration requestedExpiration;
	String revokedUid;
	boolean sessionCookieRequested;
	
	@Override
	public VerifiedToken verifyIdToken(final String token, final boolean checkRevoked) {
		failIfRejecting();
		return idToken;
	}

	@Override
	public String createSessionCookie(final String token, final Duration expiration) {
		failIfRejecting();
		sessionCookieRequested = true;
		requestedExpiration = expiration;
		return createdCookie;
	}

	@Override
	public VerifiedToken verifySessionCookie(final String cookie, final boolean checkRevoked) {
		failIfRejecting();
		return sessionToken;
	}

	@Override
	public void revokeRefreshTokens(final String uid) {
		failIfRejecting();
		revokedUid = uid;
	}
	
	private void failIfRejecting() {
		if (rejectAll) {
			throw new FirebaseRejectedException(new RuntimeException("rechazado por Firebase"));
		}
	}
}
