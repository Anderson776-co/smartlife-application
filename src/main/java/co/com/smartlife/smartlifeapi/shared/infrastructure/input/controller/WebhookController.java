package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.security.WebhookAuthenticator;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.port.InvalidateCatalogCacheInputPort;
import co.com.smartlife.smartlifeapi.shared.service.input.port.RefreshParametersInputPort;
import tools.jackson.databind.json.JsonMapper;

/**
 * Webhooks entrantes que mantienen al dia lo que el backend guarda en memoria:
 * <ul>
 * <li>{@code /strapi}: cambios en los catalogos de mensajes y notificaciones.</li>
 * <li>{@code /config}: push al repositorio de parametros (GitHub).</li>
 * </ul>
 * No usan sesion: se autentican con un secreto compartido (ver {@link WebhookAuthenticator}). Primero se
 * autentica y despues se lee el cuerpo.
 */
@RestController
@RequestMapping(WebhookController.PATH)
public class WebhookController {

	static final String PATH = "/api/v1/internal/webhooks";
	static final String STRAPI_SECRET_HEADER = "X-Webhook-Secret";
	static final String GITHUB_EVENT_HEADER = "X-GitHub-Event";
	static final String GITHUB_SIGNATURE_HEADER = "X-Hub-Signature-256";

	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);
	private static final JsonMapper JSON = JsonMapper.builder().build();

	private final WebhookAuthenticator authenticator;
	private final InvalidateCatalogCacheInputPort invalidateCatalogCache;
	private final RefreshParametersInputPort refreshParameters;

	public WebhookController(final WebhookAuthenticator authenticator,
			final InvalidateCatalogCacheInputPort invalidateCatalogCache,
			final RefreshParametersInputPort refreshParameters) {
		this.authenticator = authenticator;
		this.invalidateCatalogCache = invalidateCatalogCache;
		this.refreshParameters = refreshParameters;
	}

	@PostMapping(value = "/strapi", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> strapi(
			@RequestHeader(name = STRAPI_SECRET_HEADER, required = false) final String secret,
			@RequestBody final byte[] body) {

		if (!authenticator.isValidStrapiSecret(secret)) {
			LOGGER.warn("Webhook de Strapi rechazado: secreto ausente o inválido");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return read(body, CatalogWebhookDtoRequest.class).map(request -> {
			invalidateCatalogCache.execute(request);
			return ResponseEntity.ok().<Void>build();
		}).orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@PostMapping(value = "/config", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> config(
			@RequestHeader(name = GITHUB_EVENT_HEADER, required = false) final String event,
			@RequestHeader(name = GITHUB_SIGNATURE_HEADER, required = false) final String signature,
			@RequestBody final byte[] body) {

		if (!authenticator.isValidGitHubSignature(body, signature)) {
			LOGGER.warn("Webhook de parámetros rechazado: firma ausente o inválida");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return read(body, GitHubPayload.class).map(payload -> {
			refreshParameters.execute(new ConfigWebhookDtoRequest(event, payload.ref()));
			return ResponseEntity.ok().<Void>build();
		}).orElseGet(() -> ResponseEntity.badRequest().build());
	}

	private static <T> Optional<T> read(final byte[] body, final Class<T> type) {
		try {
			return Optional.ofNullable(JSON.readValue(body, type));
		} catch (final RuntimeException exception) {
			LOGGER.warn("Cuerpo de webhook ilegible ({})", exception.getClass().getSimpleName());
			return Optional.empty();
		}
	}

	/** Del cuerpo del evento {@code push} de GitHub solo interesa la referencia ({@code refs/heads/main}). */
	@JsonIgnoreProperties(ignoreUnknown = true)
	record GitHubPayload(String ref) {
	}
}
