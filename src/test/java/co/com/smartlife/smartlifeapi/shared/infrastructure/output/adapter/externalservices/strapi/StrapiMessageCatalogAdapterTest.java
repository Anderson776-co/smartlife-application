package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.mapper.MessageCatalogMapper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

class StrapiMessageCatalogAdapterTest {

	private static final MessageCatalogMapper MAPPER = Mappers.getMapper(MessageCatalogMapper.class);
	
	private MockRestServiceServer server;
	private StrapiMessageCatalogAdapter adapter;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new StrapiMessageCatalogAdapter(builder, MAPPER, "http://strapi.test", "test-token");
	}

	private static RequestMatcher urlContains(final String text) {
		return request -> assertTrue(request.getURI().toString().contains(text),
				"La URL " + request.getURI() + " no contiene " + text);
	}

	@Test
	void findByCodeAndLocaleShouldReturnTheMessageFromStrapi() {
		String json = "{\"data\":[{\"id\":1,\"documentId\":\"abc\",\"code\":\"VISIT_AUTHORIZED\","
				+ "\"title\":\"Autorización registrada\","
				+ "\"content\":\"La autorización de visita fue registrada correctamente.\","
				+ "\"locale\":\"es\",\"createdAt\":\"2026-10-17T00:00:00.000Z\"}],"
				+ "\"meta\":{\"pagination\":{\"page\":1}}}";

		server.expect(method(HttpMethod.GET))
				.andExpect(urlContains("/api/message-catalogs"))
				.andExpect(urlContains("VISIT_AUTHORIZED"))
				.andExpect(urlContains("locale=es"))
				.andExpect(header("Authorization", "Bearer test-token"))
				.andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

		MessageCatalogDTO result = adapter.findByCodeAndLocale("VISIT_AUTHORIZED", "es");

		assertEquals("VISIT_AUTHORIZED", result.code());
		assertEquals("Autorización registrada", result.title());
		assertEquals("La autorización de visita fue registrada correctamente.", result.content());
		assertEquals("es", result.locale());
		server.verify();
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiHasNoMessage() {
		server.expect(method(HttpMethod.GET))
				.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

		assertThrows(SmartLifeException.class, () -> adapter.findByCodeAndLocale("NO_EXISTE", "es"));
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiUrlIsNotConfigured() {
		StrapiMessageCatalogAdapter withoutUrl = new StrapiMessageCatalogAdapter(RestClient.builder(), MAPPER, "", "");

		assertThrows(SmartLifeException.class, () -> withoutUrl.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));
	}
}
