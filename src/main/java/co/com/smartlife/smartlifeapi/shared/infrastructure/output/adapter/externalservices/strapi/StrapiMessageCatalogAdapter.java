package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.dto.StrapiMessageCatalogData;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.dto.StrapiMessageCatalogResponse;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.mapper.MessageCatalogMapper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

@Component
public class StrapiMessageCatalogAdapter implements MessageCatalogOutputPort {

	private static final String QUERY_CODE = "filters[code][$eq]";
	private static final String QUERY_LOCALE = "locale";
	private static final String CODE_PLACEHOLDER = "{code}";
	private static final String LOCALE_PLACEHOLDER = "{locale}";
	
	private static final String USER_ERROR_TRYING_TO_MAKE_AN_OPERATION = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";
	private static final String MESSAGE_CATALOG_NOT_FOUND = "MESSAGE_CATALOG_NOT_FOUND";
	private static final String MESSAGE_CATALOG_UNAVAILABLE = "MESSAGE_CATALOG_UNAVAILABLE";
	private static final Set<String> RESERVED_CODES = Set.of(
			USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, MESSAGE_CATALOG_NOT_FOUND, MESSAGE_CATALOG_UNAVAILABLE);
	
	private static final String DEFAULT_USER_MESSAGE =
			"Se ha presentado un problema tratando de realizar la operación. Por favor intente de nuevo.";
	private static final String DEFAULT_NOT_FOUND_MESSAGE =
			"No se encontró en el catálogo de Strapi el mensaje con código {code} ({locale})";
	private static final String DEFAULT_UNAVAILABLE_MESSAGE =
			"No fue posible consultar el catálogo de mensajes de Strapi para el código {code} ({locale})";

	private final RestClient restClient;
	private final MessageCatalogMapper mapper;
	private final String baseUrl;
	private final String messagesPath;

	@Autowired
	public StrapiMessageCatalogAdapter(final MessageCatalogMapper mapper,
			@Value("${strapi.url:}") final String baseUrl,
			@Value("${strapi.api-token:}") final String apiToken,
			@Value("${strapi.message-catalog-path:/api/message-catalogs}") final String messagesPath,
			@Value("${strapi.connect-timeout:3s}") final Duration connectTimeout,
			@Value("${strapi.read-timeout:5s}") final Duration readTimeout) {
		this(RestClient.builder().requestFactory(requestFactory(connectTimeout, readTimeout)), mapper, baseUrl,
				apiToken, messagesPath);
	}

	public StrapiMessageCatalogAdapter(final RestClient.Builder builder, final MessageCatalogMapper mapper,
			final String baseUrl, final String apiToken, final String messagesPath) {
		this.mapper = mapper;
		this.baseUrl = baseUrl;
		this.messagesPath = messagesPath;
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
			throw unavailable(new IllegalStateException("strapi.url no esta configurada"), code, locale);
		}

		try {
			final StrapiMessageCatalogData message = query(code, locale).orElseThrow(() -> notFound(code, locale));
			return mapper.toDto(message);
		} catch (final SmartLifeException exception) {
			throw exception;
		} catch (final RuntimeException exception) {
			throw unavailable(exception, code, locale);
		}
	}

	private Optional<StrapiMessageCatalogData> query(final String code, final String locale) {
		final StrapiMessageCatalogResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.path(messagesPath)
						.queryParam(QUERY_CODE, code)
						.queryParam(QUERY_LOCALE, locale)
						.build())
				.retrieve()
				.body(StrapiMessageCatalogResponse.class);

		return Optional.ofNullable(response)
				.map(StrapiMessageCatalogResponse::data)
				.flatMap(data -> data.stream().findFirst());
	}

	private static SmartLifeException notFound(final String code, final String locale) {
		final String userMessage = catalogText(USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, locale, code)
				.orElse(DEFAULT_USER_MESSAGE);
		final String technicalTemplate = catalogText(MESSAGE_CATALOG_NOT_FOUND, locale, code)
				.orElse(DEFAULT_NOT_FOUND_MESSAGE);
		return SmartLifeException.create(userMessage, describe(technicalTemplate, code, locale));
	}

	private static SmartLifeException unavailable(final Throwable cause, final String code, final String locale) {
		final Optional<String> userMessage = catalogText(USER_ERROR_TRYING_TO_MAKE_AN_OPERATION, locale, code);
		final Optional<String> technicalTemplate = userMessage.isPresent()
				? catalogText(MESSAGE_CATALOG_UNAVAILABLE, locale, code)
				: Optional.empty();
		return SmartLifeException.create(cause, userMessage.orElse(DEFAULT_USER_MESSAGE),
				describe(technicalTemplate.orElse(DEFAULT_UNAVAILABLE_MESSAGE), code, locale));
	}

	private static Optional<String> catalogText(final String reservedCode, final String locale,
			final String requestedCode) {
		if (RESERVED_CODES.contains(requestedCode)) {
			return Optional.empty();
		}
		final String content = MessageCatalogProvider.getContent(reservedCode, locale);
		return ObjectHelper.isNull(content) || content.isBlank() || content.equals(reservedCode)
				? Optional.empty()
				: Optional.of(content);
	}

	private static String describe(final String template, final String code, final String locale) {
		final String codeText = String.valueOf(code);
		final String localeText = String.valueOf(locale);
		return template.contains(CODE_PLACEHOLDER) || template.contains(LOCALE_PLACEHOLDER)
				? template.replace(CODE_PLACEHOLDER, codeText).replace(LOCALE_PLACEHOLDER, localeText)
				: template + " (" + codeText + ", " + localeText + ")";
	}

	private static SimpleClientHttpRequestFactory requestFactory(final Duration connectTimeout,
			final Duration readTimeout) {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}
}
