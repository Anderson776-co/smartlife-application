package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.messagecatalog.strapi.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.messagecatalog.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.messagecatalog.strapi.dto.StrapiMessageCatalogResponse;
import co.com.smartlife.smartlifeapi.visits.service.output.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.MessageCatalogOutputPort;

@Component
public class StrapiMessageCatalogAdapter implements MessageCatalogOutputPort {

	private static final String MESSAGES_PATH = "/api/message-catalogs";

	private final RestClient restClient;
	private final String baseUrl;

	@Autowired
	public StrapiMessageCatalogAdapter(@Value("${strapi.url:}") final String baseUrl,
			@Value("${strapi.api-token:}") final String apiToken) {
		this(RestClient.builder(), baseUrl, apiToken);
	}

	StrapiMessageCatalogAdapter(final RestClient.Builder builder, final String baseUrl, final String apiToken) {
		this.baseUrl = baseUrl;
		this.restClient = builder.baseUrl(baseUrl).defaultHeaders(headers -> {
			if (!apiToken.isBlank()) {
				headers.setBearerAuth(apiToken);
			}
		}).build();
	}

	@Override
	public MessageCatalogDTO findByCodeAndLocale(final String code, final String locale) {
		if (baseUrl.isBlank()) {
			throw new IllegalStateException("La URL de Strapi no está configurada (strapi.url)");
		}

		StrapiMessageCatalogResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.path(MESSAGES_PATH)
						.queryParam("filters[code][$eq]", code)
						.queryParam("locale", locale)
						.build())
				.retrieve()
				.body(StrapiMessageCatalogResponse.class);

		if (response == null || response.data() == null || response.data().isEmpty()) {
			throw new IllegalStateException(
					"No se encontró el mensaje " + code + " (" + locale + ") en el catálogo de Strapi");
		}

		StrapiMessageCatalogData message = response.data().get(0);
		return new MessageCatalogDTO(message.code(), message.title(), message.content(), message.locale());
	}
}