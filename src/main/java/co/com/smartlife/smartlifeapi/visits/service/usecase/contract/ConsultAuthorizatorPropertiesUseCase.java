package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.List;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;

public interface ConsultAuthorizatorPropertiesUseCase {
	List<PropertyVisitDomain> list(AuthorizatorDomain authorizator);

}
