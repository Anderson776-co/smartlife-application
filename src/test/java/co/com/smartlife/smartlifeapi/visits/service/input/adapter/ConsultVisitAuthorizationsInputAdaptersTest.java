package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

class ConsultVisitAuthorizationsInputAdaptersTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID PROPERTY_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final Instant FROM = Instant.parse("2026-11-03T10:00:00Z");
	private static final Instant TO = Instant.parse("2026-11-04T10:00:00Z");
	private static final CurrentUser USER = new CurrentUser(USER_ID, "maria@example.com", Set.of("RESIDENT"),
			UUID.randomUUID());

	private ConsultVisitAuthorizationsUseCase useCase;
	private ListVisitAuthorizationsInputAdapter listAdapter;
	private GetVisitAuthorizationInputAdapter getAdapter;

	@BeforeEach
	void setUp() {
		useCase = mock(ConsultVisitAuthorizationsUseCase.class);
		listAdapter = new ListVisitAuthorizationsInputAdapter(useCase);
		getAdapter = new GetVisitAuthorizationInputAdapter(useCase);
	}

	private static VisitAuthorizationDomain authorization() {
		final VisitorDomain visitor = new VisitorDomain(UUID.randomUUID(), "2001", "Luis", "", "Torres", "",
				"luis@example.com", "ABC123");
		return new VisitAuthorizationDomain(UUID.randomUUID(), FROM, TO, FROM.minusSeconds(3600), "Llega en moto",
				"https://smartlife.sarl/visitas/autorizaciones/x", "visits/authorizations/x/document.png",
				AttachedDocumentDomain.empty(), PROPERTY_ID, UUID.randomUUID(), null, visitor, false,
				"Apartamento 202 - Bloque B", "Servicio tecnico", VisitAuthorizationStatus.CURRENT);
	}

	@Test
	void theListShouldAskForTheSessionUsersAuthorizationsWithTheFiltersAndReturnTheRequestedPage() {
		final List<VisitAuthorizationDomain> all = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			all.add(authorization());
		}
		when(useCase.list(USER_ID, VisitAuthorizationStatus.CURRENT, FROM, TO, PROPERTY_ID)).thenReturn(all);

		final VisitAuthorizationPageDtoResponse result = listAdapter.execute(new ListVisitAuthorizationsDtoRequest(
				VisitAuthorizationStatus.CURRENT, FROM, TO, PROPERTY_ID, USER, 1, 3));

		assertEquals(3, result.content().size());
		assertEquals(all.get(3).id(), result.content().get(0).authorizationId());
		assertEquals(all.get(3).qrCode(), result.content().get(0).qrUrl());
		assertEquals("Luis Torres", result.content().get(0).visitorFullName());
		assertEquals(VisitAuthorizationStatus.CURRENT, result.content().get(0).status());
		assertEquals(1, result.page());
		assertEquals(3, result.size());
		assertEquals(7, result.totalElements());
		assertEquals(3, result.totalPages());
	}

	@Test
	void theListShouldBeAnEmptyPageWhenThereAreNoAuthorizations() {
		when(useCase.list(USER_ID, null, null, null, null)).thenReturn(List.of());

		final VisitAuthorizationPageDtoResponse result = listAdapter
				.execute(new ListVisitAuthorizationsDtoRequest(null, null, null, null, USER));

		assertTrue(result.content().isEmpty());
		assertEquals(0, result.totalElements());
		assertEquals(0, result.totalPages());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, result.size());
	}

	@Test
	void aListRequestWithoutSessionShouldCarryTheAnonymousUser() {
		final ListVisitAuthorizationsDtoRequest request = new ListVisitAuthorizationsDtoRequest(null, null, null, null,
				null);

		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.currentUser().userId()));
		assertEquals(0, request.page());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, request.size());
	}

	@Test
	void theDetailShouldAskForTheAuthorizationOfTheSessionUserAndMapIt() {
		final VisitAuthorizationDomain authorization = authorization();
		when(useCase.find(authorization.id(), USER_ID)).thenReturn(authorization);

		final VisitAuthorizationSummaryDtoResponse result = getAdapter
				.execute(new GetVisitAuthorizationDtoRequest(authorization.id(), USER));

		assertEquals(authorization.id(), result.authorizationId());
		assertEquals("Apartamento 202 - Bloque B", result.propertyLocation());
		assertTrue(result.documentAttached());
	}

	@Test
	void theDetailShouldLetTheRejectionOfTheUseCaseThrough() {
		final UUID someoneElses = UUID.randomUUID();
		final SmartLifeException rejection = SmartLifeException.create("no existe", "no existe");
		when(useCase.find(someoneElses, USER_ID)).thenThrow(rejection);

		final GetVisitAuthorizationDtoRequest request = new GetVisitAuthorizationDtoRequest(someoneElses, USER);

		assertSame(rejection, assertThrows(SmartLifeException.class, () -> getAdapter.execute(request)));
	}

	@Test
	void aDetailRequestWithoutDataShouldCarryDefaultValues() {
		final GetVisitAuthorizationDtoRequest request = new GetVisitAuthorizationDtoRequest(null, null);

		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.authorizationId()));
		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.currentUser().userId()));
	}
}
