package co.com.smartlife.smartlifeapi.users.infrastructure.output.externalservices.identityprovider.firebase.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.users.service.output.dto.AuthenticatedUserDTO;

class FirebaseIdentityProviderAdapterTest {

	@Test
	void verifyTokenShouldReturnTheAuthenticatedUser() {
		FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter(
				_ -> new AuthenticatedUserDTO("uid-123", "residente@test.com"));

		AuthenticatedUserDTO result = adapter.verifyToken("token-valido");

		assertEquals("uid-123", result.uid());
		assertEquals("residente@test.com", result.email());
	}

	@Test
	void verifyTokenShouldFailWhenTheTokenIsEmpty() {
		FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter(
				_ -> new AuthenticatedUserDTO("uid-123", "residente@test.com"));

		assertThrows(IllegalArgumentException.class, () -> adapter.verifyToken(" "));
	}

	@Test
	void verifyTokenShouldFailWhenTheTokenIsNull() {
		FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter(
				_ -> new AuthenticatedUserDTO("uid-123", "residente@test.com"));

		assertThrows(IllegalArgumentException.class, () -> adapter.verifyToken(null));
	}

	@Test
	void verifyTokenShouldFailWhenFirebaseRejectsTheToken() {
		FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter(_ -> {
			throw new IllegalArgumentException("El token de Firebase no es válido");
		});

		assertThrows(IllegalArgumentException.class, () -> adapter.verifyToken("token-falso"));
	}

	@Test
	void verifyTokenShouldFailWhenFirebaseIsNotConfigured() {
		FirebaseIdentityProviderAdapter adapter = new FirebaseIdentityProviderAdapter("", "");

		assertThrows(IllegalStateException.class, () -> adapter.verifyToken("token-valido"));
	}
}