package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

/**
 * La autorizacion se cruza con el rango [from, to]. Un limite nulo significa "sin limite" por ese lado.
 * Dos rangos se cruzan cuando cada uno empieza antes de que el otro termine.
 */
public final class AuthorizationOverlapsRangeSpecification extends Specification<VisitAuthorizationEntity> {

	private final Instant from;
	private final Instant to;

	public AuthorizationOverlapsRangeSpecification(final Instant from, final Instant to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		if (data == null) {
			return false;
		}
		final boolean startsBeforeTheRangeEnds = to == null || data.getAuthorizationStartDate().isBefore(to);
		final boolean endsAfterTheRangeStarts = from == null || data.getAuthorizationEndDate().isAfter(from);
		return startsBeforeTheRangeEnds && endsAfterTheRangeStarts;
	}
}
