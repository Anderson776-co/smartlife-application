package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;

/** Consulta de las autorizaciones que creo un autorizador. Nadie ve las de otra persona. */
public interface ConsultVisitAuthorizationsUseCase {

	/**
	 * Las autorizaciones del autorizador que cumplen los filtros, de la mas reciente a la mas antigua. Un filtro
	 * nulo no se aplica.
	 */
	List<VisitAuthorizationDomain> list(UUID authorizatorId, VisitAuthorizationStatus status, Instant from,
			Instant to, UUID propertyId);

	/** Una autorizacion del autorizador. Si no existe o es de otra persona, se rechaza con el mismo mensaje. */
	VisitAuthorizationDomain find(UUID authorizationId, UUID authorizatorId);
}
