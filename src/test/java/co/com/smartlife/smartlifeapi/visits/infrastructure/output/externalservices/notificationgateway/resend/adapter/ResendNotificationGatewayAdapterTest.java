package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.notificationgateway.resend.adapter;

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
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.adapter.ResendNotificationGatewayAdapter;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;

class ResendNotificationGatewayAdapterTest {

	private static final String BASE_URL = "http://resend.test";

	private MockRestServiceServer server;
	private ResendNotificationGatewayAdapter adapter;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new ResendNotificationGatewayAdapter(builder, BASE_URL, "test-key", "smartlife@test.dev");
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
		ResendNotificationGatewayAdapter withoutKey = new ResendNotificationGatewayAdapter(BASE_URL, "",
				"smartlife@test.dev");

		assertThrows(IllegalStateException.class, () -> withoutKey
				.sendEmail(new EmailNotificationDTO("residente@test.com", "Asunto", "<p>Hola</p>")));
	}
}