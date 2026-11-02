package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

class AuthorizationSpecificationsTest {

	private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");
	private static final UUID PROPERTY_A = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
	private static final UUID PROPERTY_B = UUID.fromString("cccccccc-0000-0000-0000-000000000002");

	private static Instant hours(final long fromNow) {
		return NOW.plusSeconds(fromNow * 3600);
	}

	private static VisitAuthorizationEntity authorization(final Instant start, final Instant end, final boolean active,
			final UUID propertyId) {
		final PropertyVisitEntity property = propertyId == null ? null
				: new PropertyVisitEntity(propertyId, "Apartamento 202", null);
		return new VisitAuthorizationEntity(UUID.randomUUID(), start, end, NOW.minusSeconds(86400), "", "qr", null,
				active, property, null, null, null);
	}

	private static VisitAuthorizationEntity authorization(final Instant start, final Instant end, final boolean active) {
		return authorization(start, end, active, PROPERTY_A);
	}

	private static boolean matches(final VisitAuthorizationStatus status, final VisitAuthorizationEntity authorization) {
		return AuthorizationSpecifications.withStatus(status, NOW).isSatisfiedBy(authorization);
	}

	@Test
	void aCurrentAuthorizationOnlyMatchesTheCurrentStatus() {
		final VisitAuthorizationEntity current = authorization(hours(-1), hours(1), true);

		assertTrue(matches(VisitAuthorizationStatus.CURRENT, current));
		assertFalse(matches(VisitAuthorizationStatus.UPCOMING, current));
		assertFalse(matches(VisitAuthorizationStatus.EXPIRED, current));
		assertFalse(matches(VisitAuthorizationStatus.CANCELLED, current));
	}

	@Test
	void anUpcomingAuthorizationOnlyMatchesTheUpcomingStatus() {
		final VisitAuthorizationEntity upcoming = authorization(hours(1), hours(3), true);

		assertTrue(matches(VisitAuthorizationStatus.UPCOMING, upcoming));
		assertFalse(matches(VisitAuthorizationStatus.CURRENT, upcoming));
		assertFalse(matches(VisitAuthorizationStatus.EXPIRED, upcoming));
		assertFalse(matches(VisitAuthorizationStatus.CANCELLED, upcoming));
	}

	@Test
	void anExpiredAuthorizationOnlyMatchesTheExpiredStatus() {
		final VisitAuthorizationEntity expired = authorization(hours(-3), hours(-1), true);

		assertTrue(matches(VisitAuthorizationStatus.EXPIRED, expired));
		assertFalse(matches(VisitAuthorizationStatus.CURRENT, expired));
		assertFalse(matches(VisitAuthorizationStatus.UPCOMING, expired));
		assertFalse(matches(VisitAuthorizationStatus.CANCELLED, expired));
	}

	@Test
	void anInactiveAuthorizationOnlyMatchesTheCancelledStatusWhateverItsDates() {
		for (final VisitAuthorizationEntity inactive : new VisitAuthorizationEntity[] {
				authorization(hours(-1), hours(1), false), authorization(hours(1), hours(3), false),
				authorization(hours(-3), hours(-1), false) }) {
			assertTrue(matches(VisitAuthorizationStatus.CANCELLED, inactive));
			assertFalse(matches(VisitAuthorizationStatus.CURRENT, inactive));
			assertFalse(matches(VisitAuthorizationStatus.UPCOMING, inactive));
			assertFalse(matches(VisitAuthorizationStatus.EXPIRED, inactive));
		}
	}

	@Test
	void theBoundariesBelongToTheStateThatJustBegan() {
		final VisitAuthorizationEntity startsNow = authorization(NOW, hours(2), true);
		final VisitAuthorizationEntity endsNow = authorization(hours(-2), NOW, true);

		assertTrue(matches(VisitAuthorizationStatus.CURRENT, startsNow));
		assertTrue(matches(VisitAuthorizationStatus.EXPIRED, endsNow));
		assertFalse(matches(VisitAuthorizationStatus.CURRENT, endsNow));
	}

	@Test
	void everyAuthorizationHasExactlyOneStatus() {
		final VisitAuthorizationEntity[] samples = { authorization(hours(-1), hours(1), true),
				authorization(hours(1), hours(3), true), authorization(hours(-3), hours(-1), true),
				authorization(hours(-1), hours(1), false), authorization(NOW, hours(2), true),
				authorization(hours(-2), NOW, true) };

		for (final VisitAuthorizationEntity sample : samples) {
			int statuses = 0;
			for (final VisitAuthorizationStatus status : VisitAuthorizationStatus.values()) {
				if (matches(status, sample)) {
					statuses++;
				}
			}
			assertEquals(1, statuses);
		}
	}

	@Test
	void theRangeFilterKeepsTheAuthorizationsThatOverlapIt() {
		final VisitAuthorizationEntity authorization = authorization(hours(10), hours(12), true);

		assertTrue(new AuthorizationOverlapsRangeSpecification(hours(11), hours(13)).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(hours(9), hours(11)).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(hours(10), hours(12)).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(hours(12), hours(14)).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(hours(8), hours(10)).isSatisfiedBy(authorization));
	}

	@Test
	void aMissingLimitOfTheRangeMeansNoLimitOnThatSide() {
		final VisitAuthorizationEntity authorization = authorization(hours(10), hours(12), true);

		assertTrue(new AuthorizationOverlapsRangeSpecification(null, hours(11)).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(null, hours(10)).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(hours(11), null).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(hours(12), null).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(null, null).isSatisfiedBy(authorization));
	}

	@Test
	void thePropertyFilterOnlyKeepsThatProperty() {
		final VisitAuthorizationEntity inA = authorization(hours(1), hours(2), true, PROPERTY_A);
		final VisitAuthorizationEntity withoutProperty = authorization(hours(1), hours(2), true, null);

		assertTrue(new AuthorizationOfPropertySpecification(PROPERTY_A).isSatisfiedBy(inA));
		assertFalse(new AuthorizationOfPropertySpecification(PROPERTY_B).isSatisfiedBy(inA));
		assertFalse(new AuthorizationOfPropertySpecification(PROPERTY_A).isSatisfiedBy(withoutProperty));
		assertFalse(new AuthorizationOfPropertySpecification(null).isSatisfiedBy(inA));
	}

	@Test
	void theSpecificationsCombineWithAndOrAndNot() {
		final VisitAuthorizationEntity inA = authorization(hours(1), hours(2), true, PROPERTY_A);
		final Specification<VisitAuthorizationEntity> ofA = new AuthorizationOfPropertySpecification(PROPERTY_A);
		final Specification<VisitAuthorizationEntity> ofB = new AuthorizationOfPropertySpecification(PROPERTY_B);

		assertTrue(ofA.or(ofB).isSatisfiedBy(inA));
		assertFalse(ofA.and(ofB).isSatisfiedBy(inA));
		assertTrue(ofB.not().isSatisfiedBy(inA));
	}

	@Test
	void matchingCombinesOnlyTheFiltersThatAreInformed() {
		final VisitAuthorizationEntity upcomingInA = authorization(hours(10), hours(12), true, PROPERTY_A);
		final VisitAuthorizationEntity upcomingInB = authorization(hours(10), hours(12), true, PROPERTY_B);
		final VisitAuthorizationEntity currentInA = authorization(hours(-1), hours(1), true, PROPERTY_A);

		final Specification<VisitAuthorizationEntity> upcomingOfA = AuthorizationSpecifications
				.matching(VisitAuthorizationStatus.UPCOMING, hours(9), hours(13), PROPERTY_A, NOW);

		assertTrue(upcomingOfA.isSatisfiedBy(upcomingInA));
		assertFalse(upcomingOfA.isSatisfiedBy(upcomingInB));
		assertFalse(upcomingOfA.isSatisfiedBy(currentInA));
	}

	@Test
	void withoutFiltersEveryAuthorizationIsAccepted() {
		final Specification<VisitAuthorizationEntity> all = AuthorizationSpecifications.matching(null, null, null, null,
				NOW);

		assertTrue(all.isSatisfiedBy(authorization(hours(-1), hours(1), true)));
		assertTrue(all.isSatisfiedBy(authorization(hours(-3), hours(-1), false)));
		assertFalse(all.isSatisfiedBy(null));
	}
}
