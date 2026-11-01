package co.com.smartlife.smartlifeapi.shared.infrastructure.input.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Autentica los webhooks entrantes, que no traen sesion. Si un secreto no esta configurado, rechaza todo
 * (falla cerrado). Las comparaciones son de tiempo constante.
 * <ul>
 * <li>Strapi: el secreto llega tal cual en la cabecera {@code X-Webhook-Secret}.</li>
 * <li>GitHub: firma HMAC-SHA256 del cuerpo exacto en {@code X-Hub-Signature-256} ({@code sha256=<hex>}).</li>
 * </ul>
 */
@Component
public class WebhookAuthenticator {

	private static final String GITHUB_PREFIX = "sha256=";
	private static final String HMAC = "HmacSHA256";

	private final String strapiSecret;
	private final String configSecret;

	public WebhookAuthenticator(@Value("${smartlife.webhooks.strapi-secret:}") final String strapiSecret,
			@Value("${smartlife.webhooks.config-secret:}") final String configSecret) {
		this.strapiSecret = strapiSecret;
		this.configSecret = configSecret;
	}

	public boolean isValidStrapiSecret(final String received) {
		if (strapiSecret.isBlank() || received == null) {
			return false;
		}
		return MessageDigest.isEqual(sha256(strapiSecret), sha256(received));
	}

	public boolean isValidGitHubSignature(final byte[] body, final String signature) {
		if (configSecret.isBlank() || signature == null || !signature.startsWith(GITHUB_PREFIX)) {
			return false;
		}
		final String expected = HexFormat.of().formatHex(hmac(configSecret, body));
		final String received = signature.substring(GITHUB_PREFIX.length()).trim().toLowerCase(Locale.ROOT);
		return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
				received.getBytes(StandardCharsets.UTF_8));
	}

	private static byte[] sha256(final String value) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
		} catch (final GeneralSecurityException exception) {
			throw new IllegalStateException("SHA-256 no está disponible", exception);
		}
	}

	private static byte[] hmac(final String secret, final byte[] body) {
		try {
			final Mac mac = Mac.getInstance(HMAC);
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC));
			return mac.doFinal(body);
		} catch (final GeneralSecurityException exception) {
			throw new IllegalStateException("HMAC-SHA256 no está disponible", exception);
		}
	}
}
