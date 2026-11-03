package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public final class AuthorizationSpecifications {

	private AuthorizationSpecifications() {
	}

	public static Specification<VisitAuthorizationEntity> withStatus(final VisitAuthorizationStatus status,
			final Instant now) {
		final Specification<VisitAuthorizationEntity> active = new AuthorizationIsActiveSpecification();
		final Specification<VisitAuthorizationEntity> started = new AuthorizationHasStartedSpecification(now);
		final Specification<VisitAuthorizationEntity> ended = new AuthorizationHasEndedSpecification(now);
		return switch (status) {
			case CURRENT -> active.and(started).and(ended.not());
			case UPCOMING -> active.and(started.not());
			case EXPIRED -> active.and(ended);
			case CANCELLED -> active.not();
			case UNSPECIFIED -> new AuthorizationAcceptAllSpecification();
		};
	}

	public static Specification<VisitAuthorizationEntity> ofProperty(final UUID propertyId) {
		return UUIDHelper.getUUIDHelper().isDefaultUUID(propertyId) ? new AuthorizationAcceptAllSpecification()
				: new AuthorizationOfPropertySpecification(propertyId);
	}

	public static Specification<VisitAuthorizationEntity> matching(final VisitAuthorizationStatus status,
			final Instant from, final Instant to, final UUID propertyId, final Instant now) {
		return withStatus(status, now).and(new AuthorizationOverlapsRangeSpecification(from, to))
				.and(ofProperty(propertyId));
	}
}
