package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.notificationgateway.resend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

	private static RequestMatcher recordIdempotencyKey(final List<String> keys) {
		return request -> keys.add(request.getHeaders().getFirst("Idempotency-Key"));
	}

	private ResendNotificationGatewayAdapter adapterWithAttempts(final int maxAttempts) {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		return new ResendNotificationGatewayAdapter(builder, BASE_URL, "test-key", "smartlife@test.dev", EMAILS_PATH,
				maxAttempts, Duration.ZERO);
	}

	private static EmailNotificationDTO anEmail() {
		return new EmailNotificationDTO("residente@test.com", "Asunto", "<p>Hola</p>");
	}

	private static final String OK_BODY = "{\"id\":\"49a3999c-0ce1-4ea6-ab68-afcd6dc2e794\"}";

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
	void sendEmailShouldAlwaysSendAnIdempotencyKey() {
		final List<String> keys = new ArrayList<>();
		server.expect(method(HttpMethod.POST)).andExpect(recordIdempotencyKey(keys))
				.andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

		adapter.sendEmail(anEmail());

		assertEquals(1, keys.size());
		assertNotNull(keys.get(0));
		assertTrue(keys.get(0).length() >= 16);
	}

	@Test
	void shouldRetryAfterAServerErrorKeepingTheSameIdempotencyKey() {
		final ResendNotificationGatewayAdapter retrying = adapterWithAttempts(2);
		final List<String> keys = new ArrayList<>();
		server.expect(method(HttpMethod.POST)).andExpect(recordIdempotencyKey(keys)).andRespond(withServerError());
		server.expect(method(HttpMethod.POST)).andExpect(recordIdempotencyKey(keys))
				.andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

		final String id = retrying.sendEmail(anEmail());

		assertEquals("49a3999c-0ce1-4ea6-ab68-afcd6dc2e794", id);
		assertEquals(2, keys.size());
		assertEquals(keys.get(0), keys.get(1));
		server.verify();
	}

	@Test
	void shouldRetryWhenTheConnectionFails() {
		final ResendNotificationGatewayAdapter retrying = adapterWithAttempts(2);
		server.expect(method(HttpMethod.POST)).andRespond(request -> {
			throw new IOException("sin conexion");
		});
		server.expect(method(HttpMethod.POST)).andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

		final String id = retrying.sendEmail(anEmail());

		assertEquals("49a3999c-0ce1-4ea6-ab68-afcd6dc2e794", id);
		server.verify();
	}

	@Test
	void shouldRetryWhenResendLimitsTheRequests() {
		final ResendNotificationGatewayAdapter retrying = adapterWithAttempts(2);
		server.expect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
		server.expect(method(HttpMethod.POST)).andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

		final String id = retrying.sendEmail(anEmail());

		assertEquals("49a3999c-0ce1-4ea6-ab68-afcd6dc2e794", id);
		server.verify();
	}

	@Test
	void shouldNotRetryWhenResendRejectsTheRequest() {
		final ResendNotificationGatewayAdapter retrying = adapterWithAttempts(2);
		server.expect(method(HttpMethod.POST)).andRespond(withBadRequest());

		assertThrows(HttpClientErrorException.class, () -> retrying.sendEmail(anEmail()));

		server.verify();
	}

	@Test
	void shouldGiveUpAfterTheLastAttempt() {
		final ResendNotificationGatewayAdapter retrying = adapterWithAttempts(2);
		server.expect(method(HttpMethod.POST)).andRespond(withServerError());
		server.expect(method(HttpMethod.POST)).andRespond(withServerError());

		assertThrows(HttpServerErrorException.class, () -> retrying.sendEmail(anEmail()));

		server.verify();
	}

	@Test
	void shouldTryOnlyOnceWhenNoRetriesAreConfigured() {
		final ResendNotificationGatewayAdapter once = adapterWithAttempts(1);
		server.expect(method(HttpMethod.POST)).andRespond(withServerError());

		assertThrows(HttpServerErrorException.class, () -> once.sendEmail(anEmail()));

		server.verify();
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
					EMAILS_PATH, Duration.ofMillis(500), Duration.ofMillis(300), 1, Duration.ZERO);

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