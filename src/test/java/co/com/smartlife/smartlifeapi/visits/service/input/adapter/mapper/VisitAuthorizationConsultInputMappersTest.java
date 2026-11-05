package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

class VisitAuthorizationConsultInputMappersTest {

	private static final Instant FROM = Instant.parse("2026-11-03T10:00:00Z");
	private static final Instant TO = Instant.parse("2026-11-04T10:00:00Z");

	private final VisitAuthorizationFilterMapperInput filterMapper = Mappers
			.getMapper(VisitAuthorizationFilterMapperInput.class);
	private final VisitAuthorizationSummaryInputMapper summaryMapper = Mappers
			.getMapper(VisitAuthorizationSummaryInputMapper.class);

	private static VisitAuthorizationSummaryDomain summary(final boolean active) {
		return new VisitAuthorizationSummaryDomain(UUID.randomUUID(), FROM, TO, "Casa 1", "Visita familiar", true,
				"2001", "Luis Torres", "luis@example.com", "ABC123", active);
	}

	@Test
	void theFilterShouldTakeTheAuthorizatorFromTheSessionUser() {
		final UUID userId = UUID.randomUUID();
		final UUID propertyId = UUID.randomUUID();
		final CurrentUser user = new CurrentUser(userId, "maria@example.com", Set.of("RESIDENT"), UUID.randomUUID());

		final VisitAuthorizationFilterDomain result = filterMapper
				.toDomain(new ListVisitAuthorizationsDtoRequest(false, FROM, TO, propertyId, user, 2, 5));

		assertEquals(new VisitAuthorizationFilterDomain(userId, false, FROM, TO, propertyId, 2, 5), result);
	}

	@Test
	void aNullRequestShouldMapToNull() {
		assertNull(filterMapper.toDomain(null));
	}

	@Test
	void theSummaryShouldRenameTheIdToAuthorizationId() {
		final VisitAuthorizationSummaryDomain domain = summary(true);

		final VisitAuthorizationSummaryDtoResponse dto = summaryMapper.toDto(domain);

		assertEquals(domain.id(), dto.authorizationId());
		assertEquals(domain.visitorFullName(), dto.visitorFullName());
		assertEquals(domain.isActive(), dto.isActive());
	}

	@Test
	void theListShouldMapEveryElementAndANullListShouldMapToNull() {
		final List<VisitAuthorizationSummaryDtoResponse> result = summaryMapper
				.toDtoList(List.of(summary(true), summary(false)));

		assertEquals(2, result.size());
		assertEquals(Boolean.FALSE, result.get(1).isActive());
		assertNull(summaryMapper.toDtoList(null));
		assertNull(summaryMapper.toDto(null));
	}

	@Test
	void thePageShouldKeepItsNumbersAndMapItsContent() {
		final VisitAuthorizationPageDtoResponse page = summaryMapper
				.toPageDto(new VisitAuthorizationPageDomain(List.of(summary(true)), 1, 10, 11, 2));

		assertEquals(1, page.content().size());
		assertEquals(1, page.page());
		assertEquals(10, page.size());
		assertEquals(11, page.totalElements());
		assertEquals(2, page.totalPages());
		assertNull(summaryMapper.toPageDto(null));
	}
}
