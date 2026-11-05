package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;

public interface ConsultVisitAuthorizationsUseCase {

	VisitAuthorizationPageDomain list(VisitAuthorizationFilterDomain filter);

	VisitAuthorizationSummaryDomain find(UUID authorizationId, UUID authorizatorId);
}
