package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto.ResendEmailRequest;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto.ResendEmailResponse;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationGatewayOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;

@Component
public class ResendNotificationGatewayAdapter implements NotificationGatewayOutputPort {

	private static final String EMAILS_PATH = "/emails";

	private final RestClient restClient;
	private final String apiKey;
	private final String from;

	@Autowired
	public ResendNotificationGatewayAdapter(@Value("${resend.url:https://api.resend.com}") final String baseUrl,
			@Value("${resend.api-key:}") final String apiKey,
			@Value("${resend.from:onboarding@resend.dev}") final String from) {
		this(RestClient.builder(), baseUrl, apiKey, from);
	}

	public ResendNotificationGatewayAdapter(final RestClient.Builder builder, final String baseUrl, final String apiKey,
			final String from) {
		this.apiKey = apiKey;
		this.from = from;
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

		ResendEmailRequest request = new ResendEmailRequest(from, List.of(email.to()), email.subject(),
				email.content());

		ResendEmailResponse response = restClient.post()
				.uri(EMAILS_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.body(ResendEmailResponse.class);

		if (response == null || response.id() == null || response.id().isBlank()) {
			throw new IllegalStateException("Resend no devolvió el identificador del correo enviado");
		}

		return response.id();
	}
}