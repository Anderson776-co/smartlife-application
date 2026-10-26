package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi;

import java.time.Duration;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.dto.StrapiMessageCatalogResponse;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.mapper.MessageCatalogMapper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;


@Component
public class StrapiMessageCatalogAdapter implements MessageCatalogOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(StrapiMessageCatalogAdapter.class);
	
	private static final String MESSAGES_PATH = "/api/message-catalogs";
	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
	private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

	private static final String USER_ERROR_TRYING_TO_MAKE_AN_OPERATION = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";
	private static final String MESSAGE_CATALOG_NOT_FOUND = "MESSAGE_CATALOG_NOT_FOUND";
	private static final String MESSAGE_CATALOG_UNAVAILABLE = "MESSAGE_CATALOG_UNAVAILABLE";
	private static final Set<String> RESERVED_CODES = Set.of(
			USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, MESSAGE_CATALOG_NOT_FOUND, MESSAGE_CATALOG_UNAVAILABLE);
	
	private static final String DEFAULT_USER_MESSAGE =
			"Se ha presentado un problema tratando de realizar la operación. Por favor intente de nuevo.";
	private static final String DEFAULT_NOT_FOUND_MESSAGE =
			"No se encontró en el catálogo de Strapi el mensaje con código: ";
	private static final String DEFAULT_UNAVAILABLE_MESSAGE =
			"No fue posible consultar el catálogo de mensajes de Strapi para el código: ";
	private static final String DEFAULT_NOT_CONFIGURED_MESSAGE =
			"La URL de Strapi no está configurada (strapi.url). Código solicitado: ";
	
	private final RestClient restClient;
	private final MessageCatalogMapper mapper;
	private final String baseUrl;

	@Autowired
	public StrapiMessageCatalogAdapter(final MessageCatalogMapper mapper, @Value("${strapi.url:}") final String baseUrl,
			@Value("${strapi.api-token:}") final String apiToken) {
		this(RestClient.builder().requestFactory(requestFactory()), mapper, baseUrl, apiToken);
	}

	public StrapiMessageCatalogAdapter(final RestClient.Builder builder, final MessageCatalogMapper mapper, 
			final String baseUrl, final String apiToken) {
		this.mapper = mapper;
		this.baseUrl = baseUrl;
		this.restClient = builder.baseUrl(baseUrl).defaultHeaders(headers -> {
			if (!apiToken.isBlank()) {
				headers.setBearerAuth(apiToken);
			}
		}).build();
	}

	@Override
	@Cacheable(value = "message-catalog", key = "#code + ':' + #locale")
	public MessageCatalogDTO findByCodeAndLocale(final String code, final String locale) {
		if (baseUrl.isBlank()) {
			throw SmartLifeException.create(DEFAULT_USER_MESSAGE, DEFAULT_NOT_FOUND_MESSAGE + code + " (" + locale + ")");
		}
		
		final StrapiMessageCatalogResponse response;

		try {
			response = restClient.get()
					.uri(uriBuilder -> uriBuilder.path(MESSAGES_PATH)
							.queryParam("filters[code][$eq]", code)
							.queryParam("locale", locale)
							.build())
					.retrieve()
					.body(StrapiMessageCatalogResponse.class);

		} catch (final RestClientException exception) {
			LOGGER.warn("Falló la consulta a Strapi del mensaje {} ({}): {}", code, locale, exception.getMessage());
			throw SmartLifeException.create(exception,
					resolve(USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, locale, DEFAULT_USER_MESSAGE, code),
					resolve(MESSAGE_CATALOG_UNAVAILABLE, locale, DEFAULT_UNAVAILABLE_MESSAGE, code)
							+ code + " (" + locale + ")");
		}
		
		if (response == null || response.data() == null || response.data().isEmpty()) {
			throw SmartLifeException.create(
					resolve(USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, locale, DEFAULT_USER_MESSAGE, code),
					resolve(MESSAGE_CATALOG_NOT_FOUND, locale, DEFAULT_NOT_FOUND_MESSAGE, code)
							+ code + " (" + locale + ")");
		}

		StrapiMessageCatalogData message = response.data().get(0);
		return mapper.toDTO(message);
	}
	
	private static String resolve(final String messageCode, final String locale,
			final String defaultMessage, final String requestedCode) {

		if (RESERVED_CODES.contains(requestedCode)) {
			return defaultMessage;
		}

		final String content = MessageCatalogProvider.getContent(messageCode, locale);

		return content == null || content.isBlank() || content.equals(messageCode)
				? defaultMessage
				: content;
	}

	private static SimpleClientHttpRequestFactory requestFactory() {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(CONNECT_TIMEOUT);
		factory.setReadTimeout(READ_TIMEOUT);
		return factory;
	}
}