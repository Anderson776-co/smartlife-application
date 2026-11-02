package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.notificationgateway.resend;

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
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.sun.net.httpserver.HttpServer;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.ResendNotificationGatewayAdapter;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;

class ResendNotificationGatewayAdapterTest {

	private static final String BASE_URL = "http://resend.test";
	private static final String EMAILS_PATH = "/emails";

	private MockRestServiceServer server;
	private ResendNotificationGatewayAdapter adapter;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new ResendNotificationGatewayAdapter(builder, BASE_URL, "test-key", "smartlife@test.dev", EMAILS_PATH);
	}

	private static RequestMatcher urlContains(final String text) {
		return request -> assertTrue(request.getURI().toString().contains(text),
				"La URL " + request.getURI() + " no contiene " + text);
	}

	private static RequestMatcher bodyContains(final String text) {
		return request -> {
			String body = ((MockClientHttpRequest) request).getBodyAsString();
			assertTrue(body.contains(text), "El cuerpo " + body + " no contiene " + text);
		};
	}

	@Test
	void sendEmailShouldReturnTheIdFromResend() {
		server.expect(method(HttpMethod.POST))
				.andExpect(urlContains("/emails"))
				.andExpect(header("Authorization", "Bearer test-key"))
				.andExpect(bodyContains("\"from\":\"smartlife@test.dev\""))
				.andExpect(bodyContains("\"to\":[\"residente@test.com\"]"))
				.andExpect(bodyContains("\"html\":\"<p>Hola</p>\""))
				.andRespond(withSuccess("{\"id\":\"49a3999c-0ce1-4ea6-ab68-afcd6dc2e794\"}",
						MediaType.APPLICATION_JSON));

		String id = adapter.sendEmail(
				new EmailNotificationDTO("residente@test.com", "Autorización registrada", "<p>Hola</p>"));

		assertEquals("49a3999c-0ce1-4ea6-ab68-afcd6dc2e794", id);
		server.verify();
	}

	@Test
	void sendEmailShouldFailWhenResendDoesNotReturnAnId() {
		server.expect(method(HttpMethod.POST)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertThrows(IllegalStateException.class, () -> adapter
				.sendEmail(new EmailNotificationDTO("residente@test.com", "Asunto", "<p>Hola</p>")));
	}

	@Test
	void sendEmailShouldFailWhenApiKeyIsNotConfigured() {
		ResendNotificationGatewayAdapter withoutKey = new ResendNotificationGatewayAdapter(RestClient.builder(), BASE_URL,
				"", "smartlife@test.dev", EMAILS_PATH);

		assertThrows(IllegalStateException.class, () -> withoutKey
				.sendEmail(new EmailNotificationDTO("residente@test.com", "Asunto", "<p>Hola</p>")));
	}

	@Test
	void sendEmailShouldGiveUpWhenResendDoesNotAnswerInTime() throws IOException {
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
			ResendNotificationGatewayAdapter withTimeouts = new ResendNotificationGatewayAdapter(
					"http://127.0.0.1:" + slow.getAddress().getPort(), "test-key", "smartlife@test.dev",
					EMAILS_PATH, Duration.ofMillis(500), Duration.ofMillis(300));

			long start = System.nanoTime();
			assertThrows(RestClientException.class, () -> withTimeouts
					.sendEmail(new EmailNotificationDTO("residente@test.com", "Asunto", "<p>Hola</p>")));
			long elapsedMillis = Duration.ofNanos(System.nanoTime() - start).toMillis();

			assertTrue(elapsedMillis < 1200, "Tardó " + elapsedMillis + " ms: el tiempo de espera no se aplicó");
		} finally {
			slow.stop(0);
		}
	}
}