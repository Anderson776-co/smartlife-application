package co.com.smartlife.smartlifeapi.shared.infrastructure.input.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

class WebhookAuthenticatorTest {

	private static final String STRAPI_SECRET = "secreto-de-strapi";
	private static final String CONFIG_SECRET = "secreto-de-github";
	private static final byte[] BODY = "{\"ref\":\"refs/heads/main\"}".getBytes(StandardCharsets.UTF_8);

	private final WebhookAuthenticator authenticator = new WebhookAuthenticator(STRAPI_SECRET, CONFIG_SECRET);

	static String sign(final String secret, final byte[] body) throws Exception {
		final Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return "sha256=" + HexFormat.of().formatHex(mac.doFinal(body));
	}

	@Test
	void shouldAcceptTheStrapiSecret() {
		assertTrue(authenticator.isValidStrapiSecret(STRAPI_SECRET));
	}

	@Test
	void shouldRejectAWrongOrMissingStrapiSecret() {
		assertFalse(authenticator.isValidStrapiSecret("otro"));
		assertFalse(authenticator.isValidStrapiSecret(""));
		assertFalse(authenticator.isValidStrapiSecret(null));
	}

	@Test
	void shouldRejectEverythingWhenTheStrapiSecretIsNotConfigured() {
		final WebhookAuthenticator unconfigured = new WebhookAuthenticator("", CONFIG_SECRET);

		assertFalse(unconfigured.isValidStrapiSecret(""));
		assertFalse(unconfigured.isValidStrapiSecret("cualquiera"));
	}

	@Test
	void shouldAcceptAValidGitHubSignature() throws Exception {
		assertTrue(authenticator.isValidGitHubSignature(BODY, sign(CONFIG_SECRET, BODY)));
	}

	@Test
	void shouldAcceptTheSignatureInUpperCaseHex() throws Exception {
		assertTrue(authenticator.isValidGitHubSignature(BODY, sign(CONFIG_SECRET, BODY).toUpperCase().replace("SHA256=", "sha256=")));
	}

	@Test
	void shouldRejectASignatureMadeWithAnotherSecret() throws Exception {
		assertFalse(authenticator.isValidGitHubSignature(BODY, sign("otro-secreto", BODY)));
	}

	@Test
	void shouldRejectASignatureWhenTheBodyWasChanged() throws Exception {
		final String signature = sign(CONFIG_SECRET, BODY);

		assertFalse(authenticator.isValidGitHubSignature("{\"ref\":\"refs/heads/otra\"}".getBytes(StandardCharsets.UTF_8), signature));
	}

	@Test
	void shouldRejectAMissingOrMalformedSignature() {
		assertFalse(authenticator.isValidGitHubSignature(BODY, null));
		assertFalse(authenticator.isValidGitHubSignature(BODY, ""));
		assertFalse(authenticator.isValidGitHubSignature(BODY, "abcdef"));
		assertFalse(authenticator.isValidGitHubSignature(BODY, "sha1=abcdef"));
	}

	@Test
	void shouldRejectEverythingWhenTheConfigSecretIsNotConfigured() throws Exception {
		final WebhookAuthenticator unconfigured = new WebhookAuthenticator(STRAPI_SECRET, "");

		assertFalse(unconfigured.isValidGitHubSignature(BODY, sign(CONFIG_SECRET, BODY)));
		assertFalse(unconfigured.isValidGitHubSignature(BODY, "sha256="));
	}
}
