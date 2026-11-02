package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.List;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;

/** Consulta de las autorizaciones que creo un autorizador. Nadie ve las de otra persona. */
public interface ConsultVisitAuthorizationsUseCase {

	/** Las autorizaciones del autorizador que cumplen los filtros, de la mas reciente a la mas antigua. */
	List<VisitAuthorizationSummaryDomain> list(UUID authorizatorId, VisitAuthorizationFilterDomain filter);

	/** Una autorizacion del autorizador. Si no existe o es de otra persona, se rechaza con el mismo mensaje. */
	VisitAuthorizationSummaryDomain find(UUID authorizationId, UUID authorizatorId);
}
