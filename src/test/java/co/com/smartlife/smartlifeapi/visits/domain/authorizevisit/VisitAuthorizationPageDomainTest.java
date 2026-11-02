package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class VisitAuthorizationPageDomainTest {

	private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");

	private static List<VisitAuthorizationSummaryDomain> summaries(final int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> new VisitAuthorizationSummaryDomain(UUID.randomUUID(), NOW, NOW, NOW, "", "", "Casa", "Visita",
						false, "1", "Visitante " + i, "v@example.com", "", VisitAuthorizationStatus.CURRENT))
				.toList();
	}

	@Test
	void shouldTakeOnlyTheRequestedPageAndReportTheTotals() {
		final List<VisitAuthorizationSummaryDomain> all = summaries(23);

		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(all, 1, 10);

		assertEquals(10, page.content().size());
		assertEquals(all.get(10), page.content().get(0));
		assertEquals(all.get(19), page.content().get(9));
		assertEquals(1, page.page());
		assertEquals(10, page.size());
		assertEquals(23, page.totalElements());
		assertEquals(3, page.totalPages());
	}

	@Test
	void theLastPageShouldBeShorter() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(summaries(23), 2, 10);

		assertEquals(3, page.content().size());
		assertEquals(3, page.totalPages());
	}

	@Test
	void anExactMultipleShouldNotCreateAnExtraPage() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(summaries(20), 0, 10);

		assertEquals(2, page.totalPages());
	}

	@Test
	void aPagePastTheLastOneShouldBeEmptyWithTheRealTotals() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(summaries(23), 99, 10);

		assertTrue(page.content().isEmpty());
		assertEquals(99, page.page());
		assertEquals(23, page.totalElements());
		assertEquals(3, page.totalPages());
	}

	@Test
	void noResultsShouldGiveAnEmptyPageWithZeroPages() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(List.of(), 0, 10);

		assertTrue(page.content().isEmpty());
		assertEquals(0, page.totalElements());
		assertEquals(0, page.totalPages());
	}

	@Test
	void aNullListShouldBeTreatedAsEmpty() {
		assertTrue(VisitAuthorizationPageDomain.of(null, 0, 10).content().isEmpty());
	}

	@Test
	void shouldAdjustANegativePageToZero() {
		assertEquals(0, VisitAuthorizationPageDomain.of(summaries(5), -4, 10).page());
	}

	@Test
	void shouldUseTheDefaultSizeWhenTheSizeIsNotPositive() {
		assertEquals(VisitAuthorizationPageDomain.DEFAULT_SIZE, VisitAuthorizationPageDomain.of(summaries(5), 0, 0).size());
		assertEquals(VisitAuthorizationPageDomain.DEFAULT_SIZE, VisitAuthorizationPageDomain.of(summaries(5), 0, -3).size());
	}

	@Test
	void shouldCapTheSizeAtTheMaximum() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(summaries(120), 0, 5000);

		assertEquals(VisitAuthorizationPageDomain.MAX_SIZE, page.size());
		assertEquals(VisitAuthorizationPageDomain.MAX_SIZE, page.content().size());
		assertEquals(3, page.totalPages());
	}

	@Test
	void theContentShouldNotBeModifiable() {
		final VisitAuthorizationPageDomain page = VisitAuthorizationPageDomain.of(summaries(3), 0, 10);

		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
				() -> page.content().add(page.content().get(0)));
	}
}
