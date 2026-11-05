package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationFilterMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryInputMapper;
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
		final VisitAuthorizationSummaryInputMapper responseMapper = Mappers
				.getMapper(VisitAuthorizationSummaryInputMapper.class);
		listAdapter = new ListVisitAuthorizationsInputAdapter(useCase,
				Mappers.getMapper(VisitAuthorizationFilterMapperInput.class), responseMapper);
		getAdapter = new GetVisitAuthorizationInputAdapter(useCase, responseMapper);
	}

	private static VisitAuthorizationSummaryDomain summary(final boolean active) {
		return new VisitAuthorizationSummaryDomain(UUID.randomUUID(), FROM, TO, "Apartamento 202 - Bloque B",
				"Visita familiar", true, "2001", "Luis Torres", "luis@example.com", "ABC123", active);
	}

	@Test
	void theListShouldBuildTheFilterFromTheRequestAndTheSessionUserAndMapThePage() {
		final VisitAuthorizationSummaryDomain first = summary(true);
		when(useCase.list(new VisitAuthorizationFilterDomain(USER_ID, true, FROM, TO, PROPERTY_ID, 1, 3)))
				.thenReturn(new VisitAuthorizationPageDomain(List.of(first, summary(false)), 1, 3, 7, 3));

		final VisitAuthorizationPageDtoResponse result = listAdapter
				.execute(new ListVisitAuthorizationsDtoRequest(true, FROM, TO, PROPERTY_ID, USER, 1, 3));

		assertEquals(2, result.content().size());
		final VisitAuthorizationSummaryDtoResponse dto = result.content().get(0);
		assertEquals(first.id(), dto.authorizationId());
		assertEquals("Luis Torres", dto.visitorFullName());
		assertEquals("Apartamento 202 - Bloque B", dto.propertyLocation());
		assertEquals("Visita familiar", dto.visitReason());
		assertEquals(Boolean.TRUE, dto.isActive());
		assertEquals(Boolean.FALSE, result.content().get(1).isActive());
		assertEquals(1, result.page());
		assertEquals(3, result.size());
		assertEquals(7, result.totalElements());
		assertEquals(3, result.totalPages());
	}

	@Test
	void theFiltersThatAreNotInformedReachTheUseCaseAsNull() {
		when(useCase.list(new VisitAuthorizationFilterDomain(USER_ID, null, null, null, null, 0,
				ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE)))
				.thenReturn(new VisitAuthorizationPageDomain(List.of(), 0,
						ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, 0, 0));

		final VisitAuthorizationPageDtoResponse result = listAdapter
				.execute(new ListVisitAuthorizationsDtoRequest(null, null, null, null, USER));

		assertTrue(result.content().isEmpty());
		assertEquals(0, result.totalElements());
		assertEquals(0, result.totalPages());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, result.size());
	}

	@Test
	void aListRequestWithNothingInformedKeepsTheOptionalFiltersEmptyAndUsesTheDefaultPage() {
		final ListVisitAuthorizationsDtoRequest request = new ListVisitAuthorizationsDtoRequest(null, null, null, null,
				null);

		assertNull(request.isActive());
		assertNull(request.from());
		assertNull(request.to());
		assertNull(request.propertyId());
		assertTrue(UUIDHelper.getUUIDHelper().isDefaultUUID(request.currentUser().userId()));
		assertEquals(0, request.page());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, request.size());
	}

	@Test
	void thePageAndTheSizeOfTheRequestAreKeptInsideTheirLimits() {
		final ListVisitAuthorizationsDtoRequest tooSmall = new ListVisitAuthorizationsDtoRequest(null, null, null,
				null, USER, -4, 0);
		final ListVisitAuthorizationsDtoRequest tooBig = new ListVisitAuthorizationsDtoRequest(null, null, null, null,
				USER, 2, 500);

		assertEquals(0, tooSmall.page());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE, tooSmall.size());
		assertEquals(2, tooBig.page());
		assertEquals(ListVisitAuthorizationsDtoRequest.MAX_PAGE_SIZE, tooBig.size());
	}

	@Test
	void theDetailShouldAskForTheAuthorizationOfTheSessionUserAndMapIt() {
		final VisitAuthorizationSummaryDomain authorization = summary(true);
		when(useCase.find(authorization.id(), USER_ID)).thenReturn(authorization);

		final VisitAuthorizationSummaryDtoResponse result = getAdapter
				.execute(new GetVisitAuthorizationDtoRequest(authorization.id(), USER));

		assertEquals(authorization.id(), result.authorizationId());
		assertEquals("Apartamento 202 - Bloque B", result.propertyLocation());
		assertTrue(result.documentAttached());
		assertEquals(Boolean.TRUE, result.isActive());
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
