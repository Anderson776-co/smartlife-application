package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;

import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.dto.AuthenticatedUserDTO;

@Component
public class FirebaseIdentityProviderAdapter implements IdentityProviderOutputPort {

	private static final String FIREBASE_APP_NAME = "smartlife";

	interface TokenVerifier {

		AuthenticatedUserDTO verify(String idToken);
	}

	private final String projectId;
	private final String credentialsBase64;
	private TokenVerifier verifier;

	@Autowired
	public FirebaseIdentityProviderAdapter(@Value("${firebase.project-id:}") final String projectId,
			@Value("${firebase.credentials-base64:}") final String credentialsBase64) {
		this.projectId = projectId;
		this.credentialsBase64 = credentialsBase64;
	}

	FirebaseIdentityProviderAdapter(final TokenVerifier verifier) {
		this.projectId = "";
		this.credentialsBase64 = "";
		this.verifier = verifier;
	}

	@Override
	public AuthenticatedUserDTO verifyToken(final String idToken) {
		if (idToken == null || idToken.isBlank()) {
			throw new IllegalArgumentException("El token de Firebase es obligatorio");
		}
		return verifier().verify(idToken);
	}

	private synchronized TokenVerifier verifier() {
		if (verifier == null) {
			if (projectId.isBlank()) {
				throw new IllegalStateException("Firebase no está configurado (firebase.project-id)");
			}
			FirebaseAuth auth = firebaseAuth();
			verifier = idToken -> {
				try {
					FirebaseToken token = auth.verifyIdToken(idToken);
					return new AuthenticatedUserDTO(token.getUid(), token.getEmail());
				} catch (FirebaseAuthException exception) {
					throw new IllegalArgumentException("El token de Firebase no es válido", exception);
				}
			};
		}
		return verifier;
	}

	private FirebaseApp firebaseApp() {
		for (FirebaseApp app : FirebaseApp.getApps()) {
			if (FIREBASE_APP_NAME.equals(app.getName())) {
				return app;
			}
		}
		try {
			GoogleCredentials credentials = credentialsBase64.isBlank()
					? GoogleCredentials.getApplicationDefault()
					: GoogleCredentials.fromStream(
							new ByteArrayInputStream(Base64.getDecoder().decode(credentialsBase64)));
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(credentials)
					.setProjectId(projectId)
					.build();
			return FirebaseApp.initializeApp(options, FIREBASE_APP_NAME);
		} catch (IOException exception) {
			throw new IllegalStateException("No fue posible leer las credenciales de Firebase", exception);
		}
	}
	
	private FirebaseAuth firebaseAuth() {
		return FirebaseAuth.getInstance(firebaseApp());
	}

	@Override
	public String createSessionCookie(final String idToken, final Duration expiration) {
		if (idToken == null || idToken.isBlank()) {
	        throw new IllegalArgumentException(
	                "El ID token de Firebase es obligatorio");
	    }

	    if (expiration == null
	            || expiration.compareTo(Duration.ofMinutes(5)) < 0
	            || expiration.compareTo(Duration.ofDays(14)) > 0) {
	        throw new IllegalArgumentException(
	                "La duración debe estar entre 5 minutos y 14 días");
	    }

	    FirebaseAuth auth = firebaseAuth();

	    try {
	        FirebaseToken token = auth.verifyIdToken(idToken, true);

	        Object authTimeClaim = token.getClaims().get("auth_time");

	        if (!(authTimeClaim instanceof Number authTimeValue)) {
	            throw new IllegalArgumentException(
	                    "El token no contiene una fecha de autenticación válida");
	        }

	        long authTime = authTimeValue.longValue();
	        long now = Instant.now().getEpochSecond();

	        if (authTime > now || now - authTime > 300) {
	            throw new IllegalArgumentException(
	                    "Se requiere una autenticación reciente");
	        }

	        SessionCookieOptions options =
	                SessionCookieOptions.builder()
	                        .setExpiresIn(expiration.toMillis())
	                        .build();

	        return auth.createSessionCookie(idToken, options);

	    } catch (FirebaseAuthException exception) {
	        throw new IllegalArgumentException(
	                "No fue posible crear la sesión de Firebase",
	                exception);
	    }
	}

	@Override
	public AuthenticatedUserDTO verifySessionCookie(String sessionCookie) {
		if (sessionCookie == null || sessionCookie.isBlank()) {
	        throw new IllegalArgumentException(
	                "La cookie de sesión es obligatoria");
	    }

	    try {
	        FirebaseToken token = firebaseAuth()
	                .verifySessionCookie(sessionCookie, true);

	        return new AuthenticatedUserDTO(
	                token.getUid(),
	                token.getEmail());

	    } catch (FirebaseAuthException exception) {
	        throw new IllegalArgumentException(
	                "La sesión de Firebase no es válida",
	                exception);
	    }
	}
}