package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.firebase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider.VerifiedToken;

public class FirebaseIdentityProviderAdapterTest {
	
	
	private static final Instant NOW = Instant.parse("2026-10-24T17:00:00Z");
	private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

	private final FakeFirebaseAuthClient firebase = new FakeFirebaseAuthClient();
	private final FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter(firebase, CLOCK);

	private static VerifiedToken tokenAuthenticatedSecondsAgo(final long seconds) {
		return new VerifiedToken("uid-123", "residente@test.com", true, NOW.getEpochSecond() - seconds);
	}
	
	@Test
	void createSessionCookieShouldReturnTheCookieWhenLoginIsRecent() {
		firebase.idToken = tokenAuthenticatedSecondsAgo(60);

		final String cookie = adapter.createSessionCookie("id-token", Duration.ofHours(1));

		assertEquals("cookie-de-sesion", cookie);
		assertEquals(Duration.ofHours(1), firebase.requestedExpiration);
	}

	@Test
	void createSessionCookieShouldFailWhenLoginIsOlderThanFiveMinutes() {
		firebase.idToken = tokenAuthenticatedSecondsAgo(301);

		assertThrows(SmartLifeException.class,
				() -> adapter.createSessionCookie("id-token", Duration.ofHours(1)));
		assertFalse(firebase.sessionCookieRequested);
	}
}