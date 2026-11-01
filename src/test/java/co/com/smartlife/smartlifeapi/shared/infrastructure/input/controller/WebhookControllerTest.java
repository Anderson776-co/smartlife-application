package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.security.WebhookAuthenticator;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.port.InvalidateCatalogCacheInputPort;
import co.com.smartlife.smartlifeapi.shared.service.input.port.RefreshParametersInputPort;

class WebhookControllerTest {

	private static final String STRAPI_SECRET = "secreto-de-strapi";
	private static final String CONFIG_SECRET = "secreto-de-github";
	private static final String STRAPI_PATH = WebhookController.PATH + "/strapi";
	private static final String CONFIG_PATH = WebhookController.PATH + "/config";

	private static final String STRAPI_BODY = "{\"event\":\"entry.publish\",\"createdAt\":\"2026-11-01T10:00:00.000Z\","
			+ "\"model\":\"message-catalog\",\"uid\":\"api::message-catalog.message-catalog\","
			+ "\"entry\":{\"id\":1,\"documentId\":\"abc\",\"code\":\"USER_MESSAGE_VISIT_DATES_IN_THE_PAST\","
			+ "\"title\":\"t\",\"content\":\"c\",\"locale\":\"es\"}}";
	private static final String PUSH_BODY = "{\"ref\":\"refs/heads/main\",\"before\":\"0\",\"repository\":{\"name\":\"x\"}}";

	private InvalidateCatalogCacheInputPort invalidateCatalogCache;
	private RefreshParametersInputPort refreshParameters;
	private MockMvc mockMvc;

	private static String sign(final String secret, final byte[] body) throws Exception {
		final Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return "sha256=" + HexFormat.of().formatHex(mac.doFinal(body));
	}

	@BeforeEach
	void setUp() {
		invalidateCatalogCache = mock(InvalidateCatalogCacheInputPort.class);
		refreshParameters = mock(RefreshParametersInputPort.class);
		final WebhookController controller = new WebhookController(
				new WebhookAuthenticator(STRAPI_SECRET, CONFIG_SECRET), invalidateCatalogCache, refreshParameters);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void strapiWithoutSecretShouldAnswer401AndDoNothing() throws Exception {
		mockMvc.perform(post(STRAPI_PATH).contentType(MediaType.APPLICATION_JSON).content(STRAPI_BODY))
				.andExpect(status().isUnauthorized());

		verifyNoInteractions(invalidateCatalogCache);
	}

	@Test
	void strapiWithWrongSecretShouldAnswer401AndDoNothing() throws Exception {
		mockMvc.perform(post(STRAPI_PATH).header(WebhookController.STRAPI_SECRET_HEADER, "otro")
				.contentType(MediaType.APPLICATION_JSON).content(STRAPI_BODY)).andExpect(status().isUnauthorized());

		verifyNoInteractions(invalidateCatalogCache);
	}

	@Test
	void strapiWithTheRightSecretShouldPassTheEntryToTheInputPort() throws Exception {
		mockMvc.perform(post(STRAPI_PATH).header(WebhookController.STRAPI_SECRET_HEADER, STRAPI_SECRET)
				.contentType(MediaType.APPLICATION_JSON).content(STRAPI_BODY)).andExpect(status().isOk());

		final ArgumentCaptor<CatalogWebhookDtoRequest> captor = ArgumentCaptor.forClass(CatalogWebhookDtoRequest.class);
		verify(invalidateCatalogCache).execute(captor.capture());
		assertEquals("entry.publish", captor.getValue().event());
		assertEquals("message-catalog", captor.getValue().model());
		assertEquals("USER_MESSAGE_VISIT_DATES_IN_THE_PAST", captor.getValue().entry().code());
		assertEquals("es", captor.getValue().entry().locale());
	}

	@Test
	void strapiTestEventWithoutEntryShouldStillAnswer200() throws Exception {
		mockMvc.perform(post(STRAPI_PATH).header(WebhookController.STRAPI_SECRET_HEADER, STRAPI_SECRET)
				.contentType(MediaType.APPLICATION_JSON).content("{\"event\":\"trigger-test\"}"))
				.andExpect(status().isOk());

		final ArgumentCaptor<CatalogWebhookDtoRequest> captor = ArgumentCaptor.forClass(CatalogWebhookDtoRequest.class);
		verify(invalidateCatalogCache).execute(captor.capture());
		assertEquals("trigger-test", captor.getValue().event());
		assertEquals("", captor.getValue().entry().code());
	}

	@Test
	void strapiWithAnUnreadableBodyShouldAnswer400() throws Exception {
		mockMvc.perform(post(STRAPI_PATH).header(WebhookController.STRAPI_SECRET_HEADER, STRAPI_SECRET)
				.contentType(MediaType.APPLICATION_JSON).content("esto no es json")).andExpect(status().isBadRequest());

		verifyNoInteractions(invalidateCatalogCache);
	}

	@Test
	void configWithoutSignatureShouldAnswer401AndDoNothing() throws Exception {
		mockMvc.perform(post(CONFIG_PATH).header(WebhookController.GITHUB_EVENT_HEADER, "push")
				.contentType(MediaType.APPLICATION_JSON).content(PUSH_BODY)).andExpect(status().isUnauthorized());

		verifyNoInteractions(refreshParameters);
	}

	@Test
	void configWithABadSignatureShouldAnswer401AndDoNothing() throws Exception {
		mockMvc.perform(post(CONFIG_PATH).header(WebhookController.GITHUB_EVENT_HEADER, "push")
				.header(WebhookController.GITHUB_SIGNATURE_HEADER,
						sign("otro-secreto", PUSH_BODY.getBytes(StandardCharsets.UTF_8)))
				.contentType(MediaType.APPLICATION_JSON).content(PUSH_BODY)).andExpect(status().isUnauthorized());

		verifyNoInteractions(refreshParameters);
	}

	@Test
	void configWithAValidSignatureShouldPassTheEventAndTheRefToTheInputPort() throws Exception {
		mockMvc.perform(post(CONFIG_PATH).header(WebhookController.GITHUB_EVENT_HEADER, "push")
				.header(WebhookController.GITHUB_SIGNATURE_HEADER,
						sign(CONFIG_SECRET, PUSH_BODY.getBytes(StandardCharsets.UTF_8)))
				.contentType(MediaType.APPLICATION_JSON).content(PUSH_BODY)).andExpect(status().isOk());

		final ArgumentCaptor<ConfigWebhookDtoRequest> captor = ArgumentCaptor.forClass(ConfigWebhookDtoRequest.class);
		verify(refreshParameters).execute(captor.capture());
		assertEquals("push", captor.getValue().event());
		assertEquals("refs/heads/main", captor.getValue().ref());
	}

	@Test
	void configPingWithoutRefShouldAnswer200() throws Exception {
		final String ping = "{\"zen\":\"Keep it logically awesome.\",\"hook_id\":1}";

		mockMvc.perform(post(CONFIG_PATH).header(WebhookController.GITHUB_EVENT_HEADER, "ping")
				.header(WebhookController.GITHUB_SIGNATURE_HEADER,
						sign(CONFIG_SECRET, ping.getBytes(StandardCharsets.UTF_8)))
				.contentType(MediaType.APPLICATION_JSON).content(ping)).andExpect(status().isOk());

		final ArgumentCaptor<ConfigWebhookDtoRequest> captor = ArgumentCaptor.forClass(ConfigWebhookDtoRequest.class);
		verify(refreshParameters).execute(captor.capture());
		assertEquals("ping", captor.getValue().event());
		assertEquals("", captor.getValue().ref());
	}
}
