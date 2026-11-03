package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public final class AuthorizationAcceptAllSpecification extends Specification<VisitAuthorizationEntity> {

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		return true;
	}
}
