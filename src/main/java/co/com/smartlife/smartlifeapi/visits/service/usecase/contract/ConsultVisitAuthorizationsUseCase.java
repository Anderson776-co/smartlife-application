package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;

/** Consulta de las autorizaciones que creo un autorizador. Nadie ve las de otra persona. */
public interface ConsultVisitAuthorizationsUseCase {

	/**
	 * Las autorizaciones del autorizador que cumplen los filtros, de la mas reciente a la mas antigua. Ningun
	 * parametro es nulo: el valor por defecto de cada filtro (estado sin especificar, de {@link Instant#EPOCH} a
	 * {@link Instant#MAX} y propiedad por defecto) quiere decir sin filtro. Nunca devuelve nulo: sin resultados, la
	 * lista va vacia.
	 */
	List<VisitAuthorizationDomain> list(UUID authorizatorId, VisitAuthorizationStatus status, Instant from,
			Instant to, UUID propertyId);

	/** Una autorizacion del autorizador. Si no existe o es de otra persona, se rechaza con el mismo mensaje. */
	VisitAuthorizationDomain find(UUID authorizationId, UUID authorizatorId);
}
