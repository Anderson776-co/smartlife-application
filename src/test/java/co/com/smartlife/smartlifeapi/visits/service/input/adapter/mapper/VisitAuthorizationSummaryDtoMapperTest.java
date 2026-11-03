package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

class VisitAuthorizationSummaryDtoMapperTest {

	private static final Instant START = Instant.parse("2026-11-03T10:00:00Z");
	private static final Instant END = Instant.parse("2026-11-03T12:00:00Z");

	private static VisitorDomain visitor(final String middleName, final String secondLastName) {
		return new VisitorDomain(UUID.randomUUID(), "2001", "Luis", middleName, "Torres", secondLastName,
				"luis@example.com", "ABC123");
	}

	private static VisitAuthorizationDomain authorization(final Instant start, final VisitorDomain visitor,
			final String fileReference) {
		return new VisitAuthorizationDomain(UUID.randomUUID(), start, start.plusSeconds(7200), START.minusSeconds(60),
				"Llega en moto", "https://smartlife.sarl/visitas/autorizaciones/x", fileReference,
				AttachedDocumentDomain.empty(), UUID.randomUUID(), UUID.randomUUID(),
				new AuthorizatorDomain(UUID.randomUUID(), UUID.randomUUID(), Set.of()), visitor, false,
				"Apartamento 202 - Bloque B", "Servicio tecnico", VisitAuthorizationStatus.UPCOMING);
	}

	private static List<VisitAuthorizationDomain> authorizations(final int count) {
		final List<VisitAuthorizationDomain> all = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			all.add(authorization(START.plusSeconds(i * 60L), visitor("", ""), ""));
		}
		return all;
	}

	@Test
	void toDtoShouldCarryEveryFieldAndExposeTheQrCodeAsQrUrl() {
		final VisitAuthorizationDomain domain = authorization(START, visitor("Andres", "Gomez"),
				"visits/authorizations/x/document.png");

		final VisitAuthorizationSummaryDtoResponse dto = VisitAuthorizationSummaryDtoMapper.toDto(domain);

		assertEquals(domain.id(), dto.authorizationId());
		assertEquals(domain.authorizationStartDate(), dto.authorizationStartDate());
		assertEquals(domain.authorizationEndDate(), dto.authorizationEndDate());
		assertEquals(domain.registerDate(), dto.registerDate());
		assertEquals("Llega en moto", dto.observation());
		assertEquals(domain.qrCode(), dto.qrUrl());
		assertEquals("Apartamento 202 - Bloque B", dto.propertyLocation());
		assertEquals("Servicio tecnico", dto.visitReason());
		assertTrue(dto.documentAttached());
		assertEquals("2001", dto.visitorDocumentNumber());
		assertEquals("Luis Andres Torres Gomez", dto.visitorFullName());
		assertEquals("luis@example.com", dto.visitorEmail());
		assertEquals("ABC123", dto.vehiclePlate());
		assertEquals(VisitAuthorizationStatus.UPCOMING, dto.status());
	}

	@Test
	void theFullNameShouldSkipTheNamesThatAreEmpty() {
		final VisitAuthorizationSummaryDtoResponse dto = VisitAuthorizationSummaryDtoMapper
				.toDto(authorization(START, visitor("", ""), ""));

		assertEquals("Luis Torres", dto.visitorFullName());
		assertFalse(dto.documentAttached());
	}

	@Test
	void toPageShouldTakeOnlyTheRequestedPageAndReportTheTotals() {
		final List<VisitAuthorizationDomain> all = authorizations(23);

		final VisitAuthorizationPageDtoResponse page = VisitAuthorizationSummaryDtoMapper.toPage(all, 1, 10);

		assertEquals(10, page.content().size());
		assertEquals(all.get(10).id(), page.content().get(0).authorizationId());
		assertEquals(all.get(19).id(), page.content().get(9).authorizationId());
		assertEquals(1, page.page());
		assertEquals(10, page.size());
		assertEquals(23, page.totalElements());
		assertEquals(3, page.totalPages());
	}

	@Test
	void theLastPageShouldBeShorter() {
		final VisitAuthorizationPageDtoResponse page = VisitAuthorizationSummaryDtoMapper.toPage(authorizations(23), 2,
				10);

		assertEquals(3, page.content().size());
		assertEquals(3, page.totalPages());
	}

	@Test
	void anExactMultipleShouldNotCreateAnExtraPage() {
		assertEquals(2, VisitAuthorizationSummaryDtoMapper.toPage(authorizations(20), 0, 10).totalPages());
	}

	@Test
	void aPagePastTheLastOneShouldBeEmptyWithTheRealTotals() {
		final VisitAuthorizationPageDtoResponse page = VisitAuthorizationSummaryDtoMapper.toPage(authorizations(23), 99,
				10);

		assertTrue(page.content().isEmpty());
		assertEquals(99, page.page());
		assertEquals(23, page.totalElements());
		assertEquals(3, page.totalPages());
	}

	@Test
	void noResultsShouldGiveAnEmptyPageWithZeroPages() {
		final VisitAuthorizationPageDtoResponse page = VisitAuthorizationSummaryDtoMapper.toPage(List.of(), 0, 10);

		assertTrue(page.content().isEmpty());
		assertEquals(0, page.totalElements());
		assertEquals(0, page.totalPages());
	}

	@Test
	void aNegativePageShouldBeAdjustedToZero() {
		assertEquals(0, VisitAuthorizationSummaryDtoMapper.toPage(authorizations(5), -4, 10).page());
	}

	@Test
	void aSizeThatIsNotPositiveShouldUseTheDefaultSize() {
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE,
				VisitAuthorizationSummaryDtoMapper.toPage(authorizations(5), 0, 0).size());
		assertEquals(ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE,
				VisitAuthorizationSummaryDtoMapper.toPage(authorizations(5), 0, -3).size());
	}

	@Test
	void theSizeShouldBeCappedAtTheMaximum() {
		final VisitAuthorizationPageDtoResponse page = VisitAuthorizationSummaryDtoMapper
				.toPage(authorizations(120), 0, 500);

		assertEquals(ListVisitAuthorizationsDtoRequest.MAX_PAGE_SIZE, page.size());
		assertEquals(ListVisitAuthorizationsDtoRequest.MAX_PAGE_SIZE, page.content().size());
		assertEquals(3, page.totalPages());
	}

	@Test
	void theContentOfThePageShouldNotBeModifiable() {
		final List<VisitAuthorizationSummaryDtoResponse> content = VisitAuthorizationSummaryDtoMapper
				.toPage(authorizations(3), 0, 10).content();

		assertThrows(UnsupportedOperationException.class, content::clear);
	}
}
