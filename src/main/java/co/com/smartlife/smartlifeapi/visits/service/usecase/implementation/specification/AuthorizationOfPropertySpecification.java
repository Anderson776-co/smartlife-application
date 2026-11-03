package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public final class AuthorizationOfPropertySpecification extends Specification<VisitAuthorizationEntity> {

	private final UUID propertyId;

	public AuthorizationOfPropertySpecification(final UUID propertyId) {
		this.propertyId = propertyId;
	}

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		return propertyId.equals(data.getProperty().getId());
	}
}
