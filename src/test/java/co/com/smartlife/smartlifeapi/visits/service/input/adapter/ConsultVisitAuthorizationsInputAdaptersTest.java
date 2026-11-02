package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryDtoMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
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

	private static VisitAuthorizationSummaryDomain summary() {
		return new VisitAuthorizationSummaryDomain(UUID.randomUUID(), FROM, TO, FROM.minusSeconds(3600), "Llega en moto",
				"https://smartlife.sarl/visitas/autorizaciones/x", "Apartamento 202 - Bloque B", "Servicio tecnico", true,
				"2001", "Luis Torres", "luis@example.com", "ABC123", VisitAuthorizationStatus.CURRENT);
	}

	@Test
	void theListShouldAskForTheSessionUsersAuthorizationsWithTheFiltersAndMapTheResult() {
		final VisitAuthorizationSummaryDomain summary = summary();
		when(useCase.list(USER_ID,
				new VisitAuthorizationFilterDomain(VisitAuthorizationStatus.CURRENT, FROM, TO, PROPERTY_ID)))
				.thenReturn(List.of(summary));

		final List<VisitAuthorizationSummaryDtoResponse> result = listAdapter.execute(
				new ListVisitAuthorizationsDtoRequest(VisitAuthorizationStatus.CURRENT, FROM, TO, PROPERTY_ID, USER));

		assertEquals(1, result.size());
		assertEquals(summary.id(), result.get(0).authorizationId());
		assertEquals(summary.qrCode(), result.get(0).qrUrl());
		assertEquals("Luis Torres", result.get(0).visitorFullName());
		assertEquals(VisitAuthorizationStatus.CURRENT, result.get(0).status());
	}

	@Test
	void theListShouldBeEmptyWhenThereAreNoAuthorizations() {
		when(useCase.list(USER_ID, VisitAuthorizationFilterDomain.none())).thenReturn(List.of());

		assertTrue(listAdapter.execute(new ListVisitAuthorizationsDtoRequest(null, null, null, null, USER)).isEmpty());
	}

	@Test
	void aListRequestWithoutSessionShouldCarryTheAnonymousUser() {
		final ListVisitAuthorizationsDtoRequest request = new ListVisitAuthorizationsDtoRequest(null, null, null, null,
				null);

		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.currentUser().userId()));
	}

	@Test
	void theDetailShouldAskForTheAuthorizationOfTheSessionUserAndMapIt() {
		final VisitAuthorizationSummaryDomain summary = summary();
		when(useCase.find(summary.id(), USER_ID)).thenReturn(summary);

		final VisitAuthorizationSummaryDtoResponse result = getAdapter
				.execute(new GetVisitAuthorizationDtoRequest(summary.id(), USER));

		assertEquals(summary.id(), result.authorizationId());
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

	@Test
	void theMapperShouldCarryEveryFieldAndExposeTheQrCodeAsQrUrl() {
		final VisitAuthorizationSummaryDomain summary = summary();

		final VisitAuthorizationSummaryDtoResponse dto = VisitAuthorizationSummaryDtoMapper.toDto(summary);

		assertEquals(summary.id(), dto.authorizationId());
		assertEquals(summary.authorizationStartDate(), dto.authorizationStartDate());
		assertEquals(summary.authorizationEndDate(), dto.authorizationEndDate());
		assertEquals(summary.registerDate(), dto.registerDate());
		assertEquals(summary.observation(), dto.observation());
		assertEquals(summary.qrCode(), dto.qrUrl());
		assertEquals(summary.propertyLocation(), dto.propertyLocation());
		assertEquals(summary.visitReason(), dto.visitReason());
		assertEquals(summary.documentAttached(), dto.documentAttached());
		assertEquals(summary.visitorDocumentNumber(), dto.visitorDocumentNumber());
		assertEquals(summary.visitorFullName(), dto.visitorFullName());
		assertEquals(summary.visitorEmail(), dto.visitorEmail());
		assertEquals(summary.vehiclePlate(), dto.vehiclePlate());
		assertEquals(summary.status(), dto.status());
		assertFalse(dto.qrUrl().isBlank());
	}
}
