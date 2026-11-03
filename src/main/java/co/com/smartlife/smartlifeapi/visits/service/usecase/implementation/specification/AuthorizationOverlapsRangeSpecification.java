package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public final class AuthorizationOverlapsRangeSpecification extends Specification<VisitAuthorizationEntity> {

	private final Instant from;
	private final Instant to;

	public AuthorizationOverlapsRangeSpecification(final Instant from, final Instant to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		return data.getAuthorizationStartDate().isBefore(to) && data.getAuthorizationEndDate().isAfter(from);
	}
}
