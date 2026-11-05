package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class VisitAuthorizationPageDomainTest {

	@Test
	void thePageShouldKeepAnImmutableCopyOfItsContent() {
		final Instant now = Instant.parse("2026-11-03T10:00:00Z");
		final List<VisitAuthorizationSummaryDomain> source = new ArrayList<>(List.of(
				new VisitAuthorizationSummaryDomain(UUID.randomUUID(), now, now, "p", "r", false, "1", "n", "e", "",
						true)));

		final VisitAuthorizationPageDomain page = new VisitAuthorizationPageDomain(source, 0, 10, 1, 1);
		source.clear();

		assertEquals(1, page.content().size());
		assertThrows(UnsupportedOperationException.class, () -> page.content().clear());
	}
}
