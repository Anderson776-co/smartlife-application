package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi.dto.StrapiNotificationCatalogData;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi.dto.StrapiNotificationCatalogResponse;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationCatalogOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;

@Component
public class StrapiNotificationCatalogAdapter implements NotificationCatalogOutputPort {

	private final RestClient restClient;
	private final String baseUrl;
	private final String notificationsPath;

	@Autowired
	public StrapiNotificationCatalogAdapter(@Value("${strapi.url:}") final String baseUrl,
			@Value("${strapi.api-token:}") final String apiToken,
			@Value("${strapi.notification-catalog-path:/api/notification-catalogs}") final String notificationsPath,
			@Value("${strapi.connect-timeout:3s}") final Duration connectTimeout,
			@Value("${strapi.read-timeout:5s}") final Duration readTimeout) {
		this(RestClient.builder().requestFactory(requestFactory(connectTimeout, readTimeout)), baseUrl, apiToken,
				notificationsPath);
	}

	public StrapiNotificationCatalogAdapter(final RestClient.Builder builder, final String baseUrl,
			final String apiToken, final String notificationsPath) {
		this.baseUrl = baseUrl;
		this.notificationsPath = notificationsPath;
		this.restClient = builder.baseUrl(baseUrl).defaultHeaders(headers -> {
			if (!apiToken.isBlank()) {
				headers.setBearerAuth(apiToken);
			}
		}).build();
	}

	@Override
	@Cacheable(value = "notification-catalog", key = "#code + ':' + #locale")
	public NotificationCatalogDTO findByCodeAndLocale(final String code, final String locale) {
		if (baseUrl.isBlank()) {
			throw new IllegalStateException("La URL de Strapi no está configurada (strapi.url)");
		}

		StrapiNotificationCatalogResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.path(notificationsPath)
						.queryParam("filters[code][$eq]", code)
						.queryParam("locale", locale)
						.build())
				.retrieve()
				.body(StrapiNotificationCatalogResponse.class);

		if (ObjectHelper.isNull(response) || ObjectHelper.isNull(response.data()) || response.data().isEmpty()) {
			throw new IllegalStateException(
					"No se encontró la notificación " + code + " (" + locale + ") en el catálogo de Strapi");
		}

		StrapiNotificationCatalogData notification = response.data().get(0);
		return new NotificationCatalogDTO(notification.code(), notification.subject(),
				notification.content(), notification.locale());
	}

	private static SimpleClientHttpRequestFactory requestFactory(final Duration connectTimeout,
			final Duration readTimeout) {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}
}
