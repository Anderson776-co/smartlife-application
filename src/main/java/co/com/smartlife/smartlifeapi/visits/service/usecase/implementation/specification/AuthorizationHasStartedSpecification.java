package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

/** La autorizacion ya empezo: su inicio es la fecha de referencia o una anterior. */
public final class AuthorizationHasStartedSpecification extends Specification<VisitAuthorizationEntity> {

	private final Instant reference;

	public AuthorizationHasStartedSpecification(final Instant reference) {
		this.reference = reference;
	}

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		return !data.getAuthorizationStartDate().isAfter(reference);
	}
}
