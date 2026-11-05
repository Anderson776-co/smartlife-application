package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.List;

import co.com.smartlife.smartlifeapi.visits.domain.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;

public interface ConsultAuthorizatorPropertiesUseCase {
	List<PropertyVisitDomain> list(AuthorizatorDomain authorizator);

}
