package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.VerifiedToken;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.IdentityProviderOutputPort;
import co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.dto.AuthenticatedUserDTO;

@Component
public class FirebaseIdentityProviderAdapter implements IdentityProviderOutputPort {

	private static final Duration MIN_SESSION = Duration.ofMinutes(5);
	private static final Duration MAX_SESSION = Duration.ofDays(14);
	private static final long RECENT_LOGIN_SECONDS = 300;
	
	private static final String SESSION_ID_TOKEN_REQUIRED = "SESSION_ID_TOKEN_REQUIRED";
	private static final String SESSION_ID_TOKEN_INVALID = "SESSION_ID_TOKEN_INVALID";
	private static final String SESSION_NOT_CREATED = "SESSION_NOT_CREATED";
	private static final String SESSION_COOKIE_REQUIRED = "SESSION_COOKIE_REQUIRED";
	private static final String SESSION_INVALID = "SESSION_INVALID";
	private static final String SESSION_UID_REQUIRED = "SESSION_UID_REQUIRED";
	private static final String SESSION_REVOKE_FAILED = "SESSION_REVOKE_FAILED";
	private static final String SESSION_RECENT_LOGIN_REQUIRED = "SESSION_RECENT_LOGIN_REQUIRED";
	private static final String SESSION_DURATION_INVALID = "SESSION_DURATION_INVALID";
	
	private final FirebaseAuthClient client;
	private final Clock clock;

	@Autowired
	public FirebaseIdentityProviderAdapter(@Value("${firebase.project-id:}") final String projectId,
			@Value("${firebase.credentials-base64:}") final String credentialsBase64) {
		this(new FirebaseAdminAuthClient(projectId, credentialsBase64), Clock.systemUTC());
	}

	FirebaseIdentityProviderAdapter(final FirebaseAuthClient client, final Clock clock) {
		this.client = client;
		this.clock = clock;
	}

	@Override
	public AuthenticatedUserDTO verifyToken(final String idToken) {
		requireText(idToken, SESSION_ID_TOKEN_REQUIRED, "El idToken llegó vacío al verificarlo");
		final VerifiedToken token = rejectAsInvalid(
				() -> client.verifyIdToken(idToken, false), SESSION_ID_TOKEN_INVALID,
				"Firebase rechazó el idToken");
		return toDto(token);
	}

	@Override
	public String createSessionCookie(final String idToken, final Duration expiration) {
		requireText(idToken, SESSION_ID_TOKEN_REQUIRED, "El idToken llegó vacío al crear la cookie de sesión");
		requireValidDuration(expiration);

		final VerifiedToken token = rejectAsInvalid(
				() -> client.verifyIdToken(idToken, true), SESSION_NOT_CREATED,
				"Firebase rechazó el idToken (o fue revocado) al crear la cookie de sesión");
		requireRecentLogin(token.authTime());

		return rejectAsInvalid(
				() -> client.createSessionCookie(idToken, expiration), SESSION_NOT_CREATED,
				"Firebase no pudo crear la cookie de sesión");
	}

	@Override
	public AuthenticatedUserDTO verifySessionCookie(final String sessionCookie, final boolean checkRevoked) {
		requireText(sessionCookie, SESSION_COOKIE_REQUIRED, "La cookie de sesión llegó vacía");
		return toDto(rejectAsInvalid(
				() -> client.verifySessionCookie(sessionCookie, checkRevoked), SESSION_INVALID,
				"Firebase rechazó la cookie de sesión (checkRevoked=" + checkRevoked + ")"));
	}

	@Override
	public void revokeSession(final String uid){
		requireText(uid, SESSION_UID_REQUIRED, "El uid llegó vacío al revocar la sesión");
		try {
			client.revokeRefreshTokens(uid);
		} catch (FirebaseRejectedException exception) {
			throw SmartLifeException.create(exception,
					MessageCatalogProvider.getContent(SESSION_REVOKE_FAILED),
					"Firebase no pudo revocar las sesiones del uid=" + uid + ": " + exception.getMessage());
		}
	}
	
	private void requireRecentLogin(final Long authTime) {
		final long now = Instant.now(clock).getEpochSecond();
		if (authTime == null || authTime > now || now - authTime > RECENT_LOGIN_SECONDS) {
			throw SmartLifeException.createAuthentication(
					MessageCatalogProvider.getContent(SESSION_RECENT_LOGIN_REQUIRED),
					"auth_time=" + authTime + ", now=" + now + ": el login no es reciente (máx. "
							+ RECENT_LOGIN_SECONDS + " s)");
		}
	}
	
	private static void requireValidDuration(final Duration expiration) {
		if (expiration == null || expiration.compareTo(MIN_SESSION) < 0 || expiration.compareTo(MAX_SESSION) > 0) {
			throw SmartLifeException.create(new IllegalArgumentException(),
					MessageCatalogProvider.getContent(SESSION_DURATION_INVALID),
					"smartlife.session.duration=" + expiration + " está fuera del rango de 5 minutos a 14 días");
		}
	}
	
	private static <T> T rejectAsInvalid(final Supplier<T> call, final String messageCode, final String technicalMessage) {
		try {
			return call.get();
		} catch (FirebaseRejectedException exception) {
			throw SmartLifeException.createAuthentication(
					MessageCatalogProvider.getContent(messageCode),
					technicalMessage + ": " + exception.getMessage());
		}
	}
	
	private static void requireText(final String value, final String messageCode, final String technicalMessage) {
		if (value == null || value.isBlank()) {
			throw SmartLifeException.createAuthentication(
					MessageCatalogProvider.getContent(messageCode), technicalMessage);
		}
	}
	
	private static AuthenticatedUserDTO toDto(final VerifiedToken token) {
		return new AuthenticatedUserDTO(token.uid(), token.email(), token.emailVerified());
	}
}