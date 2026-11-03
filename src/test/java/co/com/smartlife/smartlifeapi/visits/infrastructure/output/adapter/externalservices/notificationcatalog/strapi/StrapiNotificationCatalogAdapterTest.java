package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.sun.net.httpserver.HttpServer;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationcatalog.strapi.StrapiNotificationCatalogAdapter;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;

class StrapiNotificationCatalogAdapterTest {

	private static final String PATH = "/api/notification-catalogs";

	private MockRestServiceServer server;
	private StrapiNotificationCatalogAdapter adapter;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new StrapiNotificationCatalogAdapter(builder, "http://strapi.test", "test-token", PATH);
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
		StrapiNotificationCatalogAdapter withoutUrl = new StrapiNotificationCatalogAdapter(RestClient.builder(), "",
				"", PATH);

		assertThrows(IllegalStateException.class,
				() -> withoutUrl.findByCodeAndLocale("VISIT_AUTHORIZED_EMAIL", "es"));
	}

	@Test
	void findByCodeAndLocaleShouldGiveUpWhenStrapiDoesNotAnswerInTime() throws IOException {
		HttpServer slow = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
		slow.createContext("/", exchange -> {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
			}
			exchange.close();
		});
		slow.start();
		try {
			StrapiNotificationCatalogAdapter withTimeouts = new StrapiNotificationCatalogAdapter(
					"http://127.0.0.1:" + slow.getAddress().getPort(), "", PATH, Duration.ofMillis(500),
					Duration.ofMillis(300));

			long start = System.nanoTime();
			assertThrows(RestClientException.class,
					() -> withTimeouts.findByCodeAndLocale("VISIT_AUTHORIZED_EMAIL", "es"));
			long elapsedMillis = Duration.ofNanos(System.nanoTime() - start).toMillis();

			assertTrue(elapsedMillis < 1200, "Tardó " + elapsedMillis + " ms: el tiempo de espera no se aplicó");
		} finally {
			slow.stop(0);
		}
	}
}
