package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.messagecatalog.strapi.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import co.com.smartlife.smartlifeapi.visits.service.output.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.MessageCatalogOutputPort;

class StrapiMessageCatalogCacheTest {

	private static final String JSON = "{\"data\":[{\"id\":1,\"code\":\"VISIT_AUTHORIZED\","
			+ "\"title\":\"Autorización registrada\","
			+ "\"content\":\"La autorización de visita fue registrada correctamente.\","
			+ "\"locale\":\"es\"}]}";

	@Configuration
	@EnableCaching
	static class CacheTestConfig {

		@Bean
		RestClient.Builder restClientBuilder() {
			return RestClient.builder();
		}

		@Bean
		MockRestServiceServer strapiServer(final RestClient.Builder builder) {
			return MockRestServiceServer.bindTo(builder).build();
		}

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("message-catalog");
		}

		@Bean
		MessageCatalogOutputPort messageCatalogOutputPort(final RestClient.Builder builder,
				final MockRestServiceServer strapiServer) {
			return new StrapiMessageCatalogAdapter(builder, "http://strapi.test", "test-token");
		}
	}

	private AnnotationConfigApplicationContext context;
	private MockRestServiceServer server;
	private MessageCatalogOutputPort port;

	@BeforeEach
	void setUp() {
		context = new AnnotationConfigApplicationContext(CacheTestConfig.class);
		server = context.getBean(MockRestServiceServer.class);
		port = context.getBean(MessageCatalogOutputPort.class);
	}

	@AfterEach
	void tearDown() {
		context.close();
	}

	@Test
	void theSecondCallWithTheSameKeyShouldBeServedFromTheCache() {
		server.expect(ExpectedCount.once(), method(HttpMethod.GET))
				.andRespond(withSuccess(JSON, MediaType.APPLICATION_JSON));

		MessageCatalogDTO first = port.findByCodeAndLocale("VISIT_AUTHORIZED", "es");
		MessageCatalogDTO second = port.findByCodeAndLocale("VISIT_AUTHORIZED", "es");

		assertEquals(first, second);
		server.verify();
	}

	@Test
	void aDifferentLocaleShouldCallStrapiAgain() {
		server.expect(ExpectedCount.times(2), method(HttpMethod.GET))
				.andRespond(withSuccess(JSON, MediaType.APPLICATION_JSON));

		port.findByCodeAndLocale("VISIT_AUTHORIZED", "es");
		port.findByCodeAndLocale("VISIT_AUTHORIZED", "en");

		server.verify();
	}
}