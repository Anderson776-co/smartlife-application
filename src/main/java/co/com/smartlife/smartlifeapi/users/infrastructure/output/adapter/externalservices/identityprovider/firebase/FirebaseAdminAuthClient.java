package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.VerifiedToken;

public class FirebaseAdminAuthClient implements FirebaseAuthClient{
	
	private static final String FIREBASE_APP_NAME = "smartlife";
	private static final String AUTH_TIME = "auth_time";

	private final String projectId;
	private final String credentialsBase64;
	private FirebaseAuth auth;
	
	FirebaseAdminAuthClient(final String projectId, final String credentialsBase64) {
		this.projectId = projectId == null ? "" : projectId;
		this.credentialsBase64 = credentialsBase64 == null ? "" : credentialsBase64;
	}
	
	@Override
	public VerifiedToken verifyIdToken(String idToken, boolean checkRevoked) {
		try {
			return toVerified(auth().verifyIdToken(idToken, checkRevoked));
		} catch (FirebaseAuthException exception) {
			throw new FirebaseRejectedException(exception);
		}
	}

	@Override
	public String createSessionCookie(String idToken, Duration expiration) {
		try {
			return auth().createSessionCookie(idToken,
					SessionCookieOptions.builder().setExpiresIn(expiration.toMillis()).build());
		} catch (FirebaseAuthException exception) {
			throw new FirebaseRejectedException(exception);
		}
	}

	@Override
	public VerifiedToken verifySessionCookie(String sessionCookie, boolean checkRevoked) {
		try {
			return toVerified(auth().verifySessionCookie(sessionCookie, checkRevoked));
		} catch (FirebaseAuthException exception) {
			throw new FirebaseRejectedException(exception);
		}
	}

	@Override
	public void revokeRefreshTokens(String uid) {
		try {
			auth().revokeRefreshTokens(uid);
		} catch (FirebaseAuthException exception) {
			throw new FirebaseRejectedException(exception);
		}
	}
	
	private static VerifiedToken toVerified(final FirebaseToken token) {
		final Object authTime = token.getClaims().get(AUTH_TIME);
		return new VerifiedToken(token.getUid(), token.getEmail(), token.isEmailVerified(),
				authTime instanceof Number number ? number.longValue() : null);
	}
	
	private synchronized FirebaseAuth auth() {
		if (auth == null) {
			if (projectId.isBlank()) {
				throw new IllegalStateException("Firebase no está configurado (firebase.project-id)");
			}
			auth = FirebaseAuth.getInstance(firebaseApp());
		}
		return auth;
	}

	private FirebaseApp firebaseApp() {
		for (FirebaseApp app : FirebaseApp.getApps()) {
			if (FIREBASE_APP_NAME.equals(app.getName())) {
				return app;
			}
		}
		try {
			final GoogleCredentials credentials = credentialsBase64.isBlank()
					? GoogleCredentials.getApplicationDefault()
					: GoogleCredentials.fromStream(
							new ByteArrayInputStream(Base64.getDecoder().decode(credentialsBase64)));
			return FirebaseApp.initializeApp(FirebaseOptions.builder()
					.setCredentials(credentials)
					.setProjectId(projectId)
					.build(), FIREBASE_APP_NAME);
		} catch (IOException exception) {
			throw new IllegalStateException("No fue posible leer las credenciales de Firebase", exception);
		}
	}
}
