package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitorDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;
import co.com.smartlife.smartlifeapi.visits.service.input.port.GetVisitAuthorizationInputPort;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ListVisitAuthorizationsInputPort;

class VisitAuthorizationControllerTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID PROPERTY_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final Instant FROM = Instant.parse("2026-11-03T10:00:00Z");
	private static final Instant TO = Instant.parse("2026-11-04T10:00:00Z");
	private static final CurrentUser USER = new CurrentUser(USER_ID, "maria@example.com", Set.of("RESIDENT"),
			UUID.randomUUID());

	private static final Boolean ANY_STATE = null;
	private static final Instant NO_LOWER_LIMIT = null;
	private static final Instant NO_UPPER_LIMIT = null;
	private static final UUID NO_PROPERTY = null;
	private static final MockMultipartFile NO_DOCUMENT = new MockMultipartFile("document", new byte[0]);

	private AuthorizeVisitInputPort authorizePort;
	private ListVisitAuthorizationsInputPort listPort;
	private GetVisitAuthorizationInputPort getPort;
	private VisitAuthorizationController controller;

	@BeforeEach
	void setUp() {
		authorizePort = mock(AuthorizeVisitInputPort.class);
		listPort = mock(ListVisitAuthorizationsInputPort.class);
		getPort = mock(GetVisitAuthorizationInputPort.class);
		controller = new VisitAuthorizationController(authorizePort, listPort, getPort);
	}

	private static VisitAuthorizationSummaryDtoResponse summary() {
		return new VisitAuthorizationSummaryDtoResponse(UUID.randomUUID(), FROM, TO, "Apartamento 202 - Bloque B",
				"Visita familiar", false, "2001", "Luis Torres", "luis@example.com", "", true);
	}

	private static AuthorizeVisitDtoRequest authorizeRequest() {
		return new AuthorizeVisitDtoRequest(PROPERTY_ID, PROPERTY_ID, FROM, TO, "", VisitorDtoRequest.empty(),
					AttachedDocumentDtoRequest.empty(), USER);
	}

	@Test
	void listShouldAnswer200WithTheAuthorizationsOfTheSessionUser() {
		final VisitAuthorizationSummaryDtoResponse summary = summary();
		final VisitAuthorizationPageDtoResponse page = new VisitAuthorizationPageDtoResponse(List.of(summary), 0, 10, 1,
				1);
		when(listPort.execute(new ListVisitAuthorizationsDtoRequest(ANY_STATE, NO_LOWER_LIMIT, NO_UPPER_LIMIT, NO_PROPERTY, USER, 0, 10)))
				.thenReturn(page);

		final ResponseEntity<Response<VisitAuthorizationPageDtoResponse>> response = controller.list(ANY_STATE, NO_LOWER_LIMIT, NO_UPPER_LIMIT, NO_PROPERTY, 0, 10, USER);

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertEquals(1, response.getBody().getData().size());
		assertSame(page, response.getBody().getData().get(0));
		assertSame(summary, response.getBody().getData().get(0).content().get(0));
	}

	@Test
	void listShouldPassEveryFilterThePageAndTheSessionUserToThePort() {
		when(listPort.execute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(new VisitAuthorizationPageDtoResponse(List.of(), 2, 5, 0, 0));

		controller.list(true, FROM, TO, PROPERTY_ID, 2, 5, USER);

		final ArgumentCaptor<ListVisitAuthorizationsDtoRequest> captor = ArgumentCaptor
				.forClass(ListVisitAuthorizationsDtoRequest.class);
		verify(listPort).execute(captor.capture());
		assertEquals(new ListVisitAuthorizationsDtoRequest(true, FROM, TO, PROPERTY_ID,
				USER, 2, 5), captor.getValue());
	}

	@Test
	void findShouldAnswer200WithTheAuthorizationOfTheSessionUser() {
		final VisitAuthorizationSummaryDtoResponse summary = summary();
		when(getPort.execute(new GetVisitAuthorizationDtoRequest(summary.authorizationId(), USER))).thenReturn(summary);

		final ResponseEntity<Response<VisitAuthorizationSummaryDtoResponse>> response = controller
				.find(summary.authorizationId(), USER);

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertSame(summary, response.getBody().getData().get(0));
	}

	@Test
	void findShouldAskThePortForTheAuthorizationWithTheSessionUser() {
		final UUID authorizationId = UUID.randomUUID();
		when(getPort.execute(org.mockito.ArgumentMatchers.any())).thenReturn(summary());

		controller.find(authorizationId, USER);

		final ArgumentCaptor<GetVisitAuthorizationDtoRequest> captor = ArgumentCaptor
				.forClass(GetVisitAuthorizationDtoRequest.class);
		verify(getPort).execute(captor.capture());
		assertEquals(new GetVisitAuthorizationDtoRequest(authorizationId, USER), captor.getValue());
	}

	@Test
	void authorizeShouldAnswer201WithTheLocationOfTheNewAuthorization() throws Exception {
		final UUID authorizationId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		when(authorizePort.execute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(new VisitAuthorizationDtoResponse(authorizationId, "qr", true));

		final ResponseEntity<Response<VisitAuthorizationDtoResponse>> response = controller.autorize(
				authorizeRequest(), NO_DOCUMENT, USER, UriComponentsBuilder.fromUriString("https://smartlife.sarl"));

		assertEquals(201, response.getStatusCode().value());
		assertEquals(URI.create("https://smartlife.sarl/api/v1/visits/authorizations/" + authorizationId),
				response.getHeaders().getLocation());
	}

	@Test
	void authorizeShouldPassTheUploadedDocumentAndTheSessionUser() throws Exception {
		when(authorizePort.execute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(new VisitAuthorizationDtoResponse(UUID.randomUUID(), "qr", true));
		final MockMultipartFile document = new MockMultipartFile("document", "factura.png", "image/png",
				new byte[] { 1, 2, 3 });

		controller.autorize(authorizeRequest(), document, USER, UriComponentsBuilder.fromUriString("https://x"));

		final ArgumentCaptor<AuthorizeVisitDtoRequest> captor = ArgumentCaptor.forClass(AuthorizeVisitDtoRequest.class);
		verify(authorizePort).execute(captor.capture());
		assertEquals("factura.png", captor.getValue().attachedDocument().fileName());
		assertEquals("image/png", captor.getValue().attachedDocument().contentType());
		assertEquals(3, captor.getValue().attachedDocument().content().length);
		assertEquals(USER, captor.getValue().authorizator());
	}

	@Test
	void authorizeWithoutDocumentShouldPassAnEmptyOne() throws Exception {
		when(authorizePort.execute(org.mockito.ArgumentMatchers.any()))
				.thenReturn(new VisitAuthorizationDtoResponse(UUID.randomUUID(), "qr", true));

		controller.autorize(authorizeRequest(), NO_DOCUMENT, USER, UriComponentsBuilder.fromUriString("https://x"));

		final ArgumentCaptor<AuthorizeVisitDtoRequest> captor = ArgumentCaptor.forClass(AuthorizeVisitDtoRequest.class);
		verify(authorizePort).execute(captor.capture());
		assertEquals(0, captor.getValue().attachedDocument().content().length);
	}
}
