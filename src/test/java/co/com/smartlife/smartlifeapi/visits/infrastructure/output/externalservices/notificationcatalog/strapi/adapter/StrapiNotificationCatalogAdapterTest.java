package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.notificationcatalog.strapi.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi.adapter.StrapiNotificationCatalogAdapter;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;

class StrapiNotificationCatalogAdapterTest {

	private MockRestServiceServer server;
	private StrapiNotificationCatalogAdapter adapter;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new StrapiNotificationCatalogAdapter(builder, "http://strapi.test", "test-token");
	}

	private static RequestMatcher urlContains(final String text) {
		return request -> assertTrue(request.getURI().toString().contains(text),
				"La URL " + request.getURI() + " no contiene " + text);
	}

	@Test
	void findByCodeAndLocaleShouldReturnTheNotificationFromStrapi() {
		String json = "{\"data\":[{\"id\":1,\"documentId\":\"abc\",\"code\":\"VISIT_AUTHORIZED_EMAIL\","
				+ "\"subject\":\"Autorización de visita registrada\","
				+ "\"content\":\"<p>Se registró tu autorización de visita.</p>\","
				+ "\"locale\":\"es\",\"createdAt\":\"2026-10-18T00:00:00.000Z\"}],"
				+ "\"meta\":{\"pagination\":{\"page\":1}}}";

		server.expect(method(HttpMethod.GET))
				.andExpect(urlContains("/api/notification-catalogs"))
				.andExpect(urlContains("VISIT_AUTHORIZED_EMAIL"))
				.andExpect(urlContains("locale=es"))
				.andExpect(header("Authorization", "Bearer test-token"))
				.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

		NotificationCatalogDTO result = adapter.findByCodeAndLocale("VISIT_AUTHORIZED_EMAIL", "es");

		assertEquals("VISIT_AUTHORIZED_EMAIL", result.code());
		assertEquals("Autorización de visita registrada", result.subject());
		assertEquals("<p>Se registró tu autorización de visita.</p>", result.content());
		assertEquals("es", result.locale());
		server.verify();
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiHasNoNotification() {
		server.expect(method(HttpMethod.GET))
				.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

		assertThrows(IllegalStateException.class, () -> adapter.findByCodeAndLocale("NO_EXISTE", "es"));
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiUrlIsNotConfigured() {
		StrapiNotificationCatalogAdapter withoutUrl = new StrapiNotificationCatalogAdapter("", "");

		assertThrows(IllegalStateException.class,
				() -> withoutUrl.findByCodeAndLocale("VISIT_AUTHORIZED_EMAIL", "es"));
	}
}