package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestClient;

import com.sun.net.httpserver.HttpServer;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi.mapper.MessageCatalogMapper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

class StrapiMessageCatalogAdapterTest {

	private static final MessageCatalogMapper MAPPER = Mappers.getMapper(MessageCatalogMapper.class);
	private static final String PATH = "/api/message-catalogs";
	private static final String USER_ERROR = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";
	private static final String UNAVAILABLE = "MESSAGE_CATALOG_UNAVAILABLE";
	private static final String NOT_FOUND = "MESSAGE_CATALOG_NOT_FOUND";
	private static final String DEFAULT_USER_MESSAGE_START = "Se ha presentado un problema";

	private MockRestServiceServer server;
	private StrapiMessageCatalogAdapter adapter;
	private MessageCatalogOutputPort previousPort;

	@BeforeEach
	void setUp() {
		// El proveedor es estatico y lo comparten otros tests: se guarda lo que habia y se deja en blanco
		previousPort = (MessageCatalogOutputPort) ReflectionTestUtils.getField(MessageCatalogProvider.class,
				"messageCatalogOutputPort");
		MessageCatalogProvider.init(null);

		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		adapter = new StrapiMessageCatalogAdapter(builder, MAPPER, "http://strapi.test", "test-token", PATH);
	}

	@AfterEach
	void tearDown() {
		MessageCatalogProvider.init(previousPort);
	}

	private static RequestMatcher urlContains(final String text) {
		return request -> assertTrue(request.getURI().toString().contains(text),
				"La URL " + request.getURI() + " no contiene " + text);
	}

	private static String entry(final String code, final String content) {
		return "{\"data\":[{\"id\":1,\"code\":\"" + code + "\",\"title\":\"t\",\"content\":\"" + content
				+ "\",\"locale\":\"es\"}]}";
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
	void findByCodeAndLocaleShouldUseTheConfiguredPath() {
		RestClient.Builder builder = RestClient.builder();
		MockRestServiceServer otherServer = MockRestServiceServer.bindTo(builder).build();
		StrapiMessageCatalogAdapter other = new StrapiMessageCatalogAdapter(builder, MAPPER, "http://strapi.test",
				"", "/otra/ruta");
		otherServer.expect(method(HttpMethod.GET)).andExpect(urlContains("/otra/ruta"))
				.andRespond(withSuccess(entry("X", "texto"), MediaType.APPLICATION_JSON));

		assertEquals("texto", other.findByCodeAndLocale("X", "es").content());
		otherServer.verify();
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiHasNoMessage() {
		server.expect(method(HttpMethod.GET))
				.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

		assertThrows(SmartLifeException.class, () -> adapter.findByCodeAndLocale("NO_EXISTE", "es"));
	}

	@Test
	void notFoundShouldExplainItWithTheCatalogMessages() {
		MessageCatalogProvider.init(adapter);
		server.expect(method(HttpMethod.GET)).andExpect(urlContains("NO_EXISTE"))
				.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(USER_ERROR))
				.andRespond(withSuccess(entry(USER_ERROR, "Ocurrió un error. Intenta de nuevo."),
						MediaType.APPLICATION_JSON));
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(NOT_FOUND))
				.andRespond(withSuccess(entry(NOT_FOUND, "No existe el mensaje {code} en {locale}"),
						MediaType.APPLICATION_JSON));

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale("NO_EXISTE", "es"));

		assertEquals("Ocurrió un error. Intenta de nuevo.", exception.getUserMessage());
		assertEquals("No existe el mensaje NO_EXISTE en es", exception.getTechnicalMessage());
		server.verify();
	}

	@Test
	void catalogTextWithoutPlaceholdersShouldGetTheCodeAndLocaleAppended() {
		MessageCatalogProvider.init(adapter);
		server.expect(method(HttpMethod.GET)).andExpect(urlContains("NO_EXISTE"))
				.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(USER_ERROR))
				.andRespond(withSuccess(entry(USER_ERROR, "Error."), MediaType.APPLICATION_JSON));
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(NOT_FOUND))
				.andRespond(withSuccess(entry(NOT_FOUND, "No encontramos el mensaje solicitado."),
						MediaType.APPLICATION_JSON));

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale("NO_EXISTE", "es"));

		assertEquals("No encontramos el mensaje solicitado. (NO_EXISTE, es)", exception.getTechnicalMessage());
	}

	@Test
	void findByCodeAndLocaleShouldFailAsSmartLifeExceptionWhenStrapiFails() {
		MessageCatalogProvider.init(adapter);
		server.expect(method(HttpMethod.GET)).andExpect(urlContains("VISIT_AUTHORIZED"))
				.andRespond(withServerError());
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(USER_ERROR))
				.andRespond(withSuccess(entry(USER_ERROR, "Ocurrió un error. Intenta de nuevo."),
						MediaType.APPLICATION_JSON));
		server.expect(method(HttpMethod.GET)).andExpect(urlContains(UNAVAILABLE))
				.andRespond(withSuccess(entry(UNAVAILABLE, "Catálogo no disponible para {code} ({locale})"),
						MediaType.APPLICATION_JSON));

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));

		assertEquals(SmartLifeException.Type.TECHNICAL, exception.getType());
		assertEquals("Ocurrió un error. Intenta de nuevo.", exception.getUserMessage());
		assertEquals("Catálogo no disponible para VISIT_AUTHORIZED (es)", exception.getTechnicalMessage());
		server.verify();
	}

	@Test
	void findByCodeAndLocaleShouldNotAskTheCatalogAgainWhenItIsDown() {
		MessageCatalogProvider.init(adapter);
		// Solo se esperan 2 llamadas: el mensaje y el primer texto reservado. Una tercera hace fallar el test.
		server.expect(ExpectedCount.times(2), method(HttpMethod.GET)).andRespond(withServerError());

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));

		assertTrue(exception.getUserMessage().startsWith(DEFAULT_USER_MESSAGE_START));
		assertTrue(exception.getTechnicalMessage().contains("VISIT_AUTHORIZED"));
		server.verify();
	}

	@Test
	void findByCodeAndLocaleShouldFailAsSmartLifeExceptionWhenStrapiAnswersSomethingUnexpected() {
		server.expect(method(HttpMethod.GET))
				.andRespond(withSuccess("<html>Bad gateway</html>", MediaType.TEXT_HTML));

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));

		assertEquals(SmartLifeException.Type.TECHNICAL, exception.getType());
	}

	@Test
	void reservedCodesShouldNeverAskTheCatalogToExplainThemselves() {
		MessageCatalogProvider.init(adapter);
		server.expect(ExpectedCount.once(), method(HttpMethod.GET)).andRespond(withServerError());

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> adapter.findByCodeAndLocale(USER_ERROR, "es"));

		assertTrue(exception.getUserMessage().startsWith(DEFAULT_USER_MESSAGE_START));
		server.verify();
	}

	@Test
	void findByCodeAndLocaleShouldFailWhenStrapiUrlIsNotConfigured() {
		StrapiMessageCatalogAdapter withoutUrl = new StrapiMessageCatalogAdapter(RestClient.builder(), MAPPER, "", "",
				PATH);

		SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> withoutUrl.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));

		assertEquals(SmartLifeException.Type.TECHNICAL, exception.getType());
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
			StrapiMessageCatalogAdapter withTimeouts = new StrapiMessageCatalogAdapter(MAPPER,
					"http://127.0.0.1:" + slow.getAddress().getPort(), "", PATH, Duration.ofMillis(500),
					Duration.ofMillis(300));
			MessageCatalogProvider.init(withTimeouts);

			long start = System.nanoTime();
			assertThrows(SmartLifeException.class, () -> withTimeouts.findByCodeAndLocale("VISIT_AUTHORIZED", "es"));
			long elapsedMillis = Duration.ofNanos(System.nanoTime() - start).toMillis();

			// 300 ms del mensaje + 300 ms del primer texto reservado; sin el limite esperaria los 1500 ms por llamada
			assertTrue(elapsedMillis < 1200, "Tardó " + elapsedMillis + " ms: el tiempo de espera no se aplicó");
		} finally {
			slow.stop(0);
		}
	}
}
