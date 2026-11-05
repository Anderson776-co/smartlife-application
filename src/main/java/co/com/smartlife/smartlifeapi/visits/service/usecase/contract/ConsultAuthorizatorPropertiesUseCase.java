package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.Optional;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;

public interface ConsultAuthorizatorPropertiesUseCase {
	Optional<PropertyVisitDomain> list(AuthorizatorDomain authorizator);

}
