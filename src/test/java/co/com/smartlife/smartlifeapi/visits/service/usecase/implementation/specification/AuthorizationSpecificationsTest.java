package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class AuthorizationSpecificationsTest {

	private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");
	private static final UUID PROPERTY_A = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
	private static final UUID PROPERTY_B = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final UUID NO_PROPERTY = UUIDHelper.getUUIDHelper().getDefault();

	private static Instant hours(final long fromNow) {
		return NOW.plusSeconds(fromNow * 3600);
	}

	private static VisitAuthorizationEntity authorization(final Instant start, final Instant end, final boolean active,
			final UUID propertyId) {
		final PropertyVisitEntity property = new PropertyVisitEntity(propertyId, "Apartamento 202", UUID.randomUUID());
		final AuthorizatorEntity authorizator = new AuthorizatorEntity(UUID.randomUUID(), "Maria Perez", true,
				UUID.randomUUID());
		final VisitorEntity visitor = new VisitorEntity(UUID.randomUUID(), "Luis", "", "Torres", "", "2001",
				"luis@example.com", "");
		final VisitReasonEntity reason = new VisitReasonEntity(UUID.randomUUID(), "Visita familiar", false);
		return new VisitAuthorizationEntity(UUID.randomUUID(), start, end, NOW.minusSeconds(86400), "", "qr", "",
				active, property, authorizator, visitor, reason);
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
	void everyAuthorizationHasExactlyOneSpecifiedStatus() {
		final VisitAuthorizationEntity[] samples = { authorization(hours(-1), hours(1), true),
				authorization(hours(1), hours(3), true), authorization(hours(-3), hours(-1), true),
				authorization(hours(-1), hours(1), false), authorization(NOW, hours(2), true),
				authorization(hours(-2), NOW, true) };

		for (final VisitAuthorizationEntity sample : samples) {
			int statuses = 0;
			for (final VisitAuthorizationStatus status : VisitAuthorizationStatus.values()) {
				if (status.isSpecified() && matches(status, sample)) {
					statuses++;
				}
			}
			assertEquals(1, statuses);
		}
	}

	@Test
	void theUnspecifiedStatusAcceptsEveryAuthorization() {
		assertTrue(matches(VisitAuthorizationStatus.UNSPECIFIED, authorization(hours(-1), hours(1), true)));
		assertTrue(matches(VisitAuthorizationStatus.UNSPECIFIED, authorization(hours(1), hours(3), true)));
		assertTrue(matches(VisitAuthorizationStatus.UNSPECIFIED, authorization(hours(-3), hours(-1), true)));
		assertTrue(matches(VisitAuthorizationStatus.UNSPECIFIED, authorization(hours(-1), hours(1), false)));
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
	void anUnboundedSideOfTheRangeIsTheEpochOrTheMaximumInstant() {
		final VisitAuthorizationEntity authorization = authorization(hours(10), hours(12), true);

		assertTrue(new AuthorizationOverlapsRangeSpecification(Instant.EPOCH, hours(11)).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(Instant.EPOCH, hours(10)).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(hours(11), Instant.MAX).isSatisfiedBy(authorization));
		assertFalse(new AuthorizationOverlapsRangeSpecification(hours(12), Instant.MAX).isSatisfiedBy(authorization));
		assertTrue(new AuthorizationOverlapsRangeSpecification(Instant.EPOCH, Instant.MAX)
				.isSatisfiedBy(authorization));
	}

	@Test
	void thePropertyFilterOnlyKeepsThatProperty() {
		final VisitAuthorizationEntity inA = authorization(hours(1), hours(2), true, PROPERTY_A);

		assertTrue(new AuthorizationOfPropertySpecification(PROPERTY_A).isSatisfiedBy(inA));
		assertFalse(new AuthorizationOfPropertySpecification(PROPERTY_B).isSatisfiedBy(inA));
	}

	@Test
	void theDefaultPropertyMeansNoPropertyFilter() {
		final VisitAuthorizationEntity inA = authorization(hours(1), hours(2), true, PROPERTY_A);
		final VisitAuthorizationEntity inB = authorization(hours(1), hours(2), true, PROPERTY_B);

		assertTrue(AuthorizationSpecifications.ofProperty(NO_PROPERTY).isSatisfiedBy(inA));
		assertTrue(AuthorizationSpecifications.ofProperty(NO_PROPERTY).isSatisfiedBy(inB));
		assertTrue(AuthorizationSpecifications.ofProperty(PROPERTY_A).isSatisfiedBy(inA));
		assertFalse(AuthorizationSpecifications.ofProperty(PROPERTY_A).isSatisfiedBy(inB));
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
	void matchingCombinesTheFourFilters() {
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
	void withEveryFilterAtItsDefaultEveryAuthorizationIsAccepted() {
		final Specification<VisitAuthorizationEntity> all = AuthorizationSpecifications.matching(
				VisitAuthorizationStatus.UNSPECIFIED, Instant.EPOCH, Instant.MAX, NO_PROPERTY, NOW);

		assertTrue(all.isSatisfiedBy(authorization(hours(-1), hours(1), true)));
		assertTrue(all.isSatisfiedBy(authorization(hours(-3), hours(-1), false, PROPERTY_B)));
		assertTrue(all.isSatisfiedBy(authorization(hours(5), hours(6), true, PROPERTY_B)));
	}

	@Test
	void theAcceptAllSpecificationAcceptsAnyAuthorization() {
		assertTrue(new AuthorizationAcceptAllSpecification().isSatisfiedBy(authorization(hours(1), hours(2), false)));
	}
}
