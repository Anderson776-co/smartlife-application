package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;


public interface ConsultVisitAuthorizationsUseCase {

	
	List<VisitAuthorizationDomain> list(UUID authorizatorId, VisitAuthorizationStatus status, Instant from,
			Instant to, UUID propertyId);

	VisitAuthorizationDomain find(UUID authorizationId, UUID authorizatorId);
}
