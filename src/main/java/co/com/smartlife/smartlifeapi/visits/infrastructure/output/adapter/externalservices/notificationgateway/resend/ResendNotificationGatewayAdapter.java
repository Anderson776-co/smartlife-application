package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto.ResendAttachment;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto.ResendEmailRequest;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto.ResendEmailResponse;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationGatewayOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;

@Component
public class ResendNotificationGatewayAdapter implements NotificationGatewayOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationGatewayAdapter.class);
	private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

	private final RestClient restClient;
	private final String apiKey;
	private final String from;
	private final String emailsPath;
	private final int maxAttempts;
	private final Duration retryDelay;

	@Autowired
	public ResendNotificationGatewayAdapter(@Value("${resend.url:https://api.resend.com}") final String baseUrl,
			@Value("${resend.api-key:}") final String apiKey,
			@Value("${resend.from:onboarding@resend.dev}") final String from,
			@Value("${resend.emails-path:/emails}") final String emailsPath,
			@Value("${resend.connect-timeout:3s}") final Duration connectTimeout,
			@Value("${resend.read-timeout:10s}") final Duration readTimeout,
			@Value("${resend.max-attempts:2}") final int maxAttempts,
			@Value("${resend.retry-delay:500ms}") final Duration retryDelay) {
		this(RestClient.builder().requestFactory(requestFactory(connectTimeout, readTimeout)), baseUrl, apiKey, from,
				emailsPath, maxAttempts, retryDelay);
	}

	public ResendNotificationGatewayAdapter(final RestClient.Builder builder, final String baseUrl,
			final String apiKey, final String from, final String emailsPath) {
		this(builder, baseUrl, apiKey, from, emailsPath, 1, Duration.ZERO);
	}

	public ResendNotificationGatewayAdapter(final RestClient.Builder builder, final String baseUrl,
			final String apiKey, final String from, final String emailsPath, final int maxAttempts,
			final Duration retryDelay) {
		this.apiKey = apiKey;
		this.from = from;
		this.emailsPath = emailsPath;
		this.maxAttempts = Math.max(1, maxAttempts);
		this.retryDelay = retryDelay;
		this.restClient = builder.baseUrl(baseUrl).defaultHeaders(headers -> {
			if (!apiKey.isBlank()) {
				headers.setBearerAuth(apiKey);
			}
		}).build();
	}

	@Override
	public String sendEmail(final EmailNotificationDTO email) {
		if (apiKey.isBlank()) {
			throw new IllegalStateException("La API key de Resend no está configurada (resend.api-key)");
		}

		final List<ResendAttachment> attachments = email.attachments().stream()
				.map(a -> new ResendAttachment(a.fileName(), a.base64Content(), a.contentType(), a.contentId()))
				.toList();
		final ResendEmailRequest request = new ResendEmailRequest(from, List.of(email.to()), email.subject(),
				email.content(), attachments);
		final String idempotencyKey = UUID.randomUUID().toString();

		for (int attempt = 1;; attempt++) {
			try {
				return send(request, idempotencyKey);
			} catch (final RuntimeException exception) {
				if (attempt >= maxAttempts || !isTransient(exception)) {
					throw exception;
				}
				LOGGER.warn("Resend no respondió bien (intento {} de {}): {}. Se reintenta.", attempt, maxAttempts,
						exception.getClass().getSimpleName());
				pause();
			}
		}
	}

	private String send(final ResendEmailRequest request, final String idempotencyKey) {
		final ResendEmailResponse response = restClient.post()
				.uri(emailsPath)
				.contentType(MediaType.APPLICATION_JSON)
				.header(IDEMPOTENCY_HEADER, idempotencyKey)
				.body(request)
				.retrieve()
				.body(ResendEmailResponse.class);

		if (ObjectHelper.isNull(response) || ObjectHelper.isNull(response.id()) || response.id().isBlank()) {
			throw new IllegalStateException("Resend no devolvió el identificador del correo enviado");
		}

		return response.id();
	}

	private static boolean isTransient(final RuntimeException exception) {
		return exception instanceof ResourceAccessException || exception instanceof HttpServerErrorException
				|| (exception instanceof HttpClientErrorException client
						&& client.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value());
	}

	private void pause() {
		if (retryDelay.isZero() || retryDelay.isNegative()) {
			return;
		}
		try {
			Thread.sleep(retryDelay.toMillis());
		} catch (final InterruptedException interrupted) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("El envío del correo se interrumpió", interrupted);
		}
	}

	private static SimpleClientHttpRequestFactory requestFactory(final Duration connectTimeout,
			final Duration readTimeout) {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}
}