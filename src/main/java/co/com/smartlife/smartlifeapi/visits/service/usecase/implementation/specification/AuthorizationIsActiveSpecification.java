package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public final class AuthorizationIsActiveSpecification extends Specification<VisitAuthorizationEntity> {

	@Override
	public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
		return ObjectHelper.getDefault(data.getIsActive(), Boolean.FALSE);
	}
}
