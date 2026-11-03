package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

/**
 * Arma los filtros de la consulta de autorizaciones combinando Specification con and, or y not.
 * Los estados se componen a partir de tres condiciones simples: activa, ya empezo y ya termino.
 * Ningun filtro llega nulo: el valor por defecto de cada uno es el "sin filtro" y se traduce en una Specification
 * que acepta todo.
 */
public final class AuthorizationSpecifications {

	private AuthorizationSpecifications() {
	}

	/** Estado de la autorizacion segun la fecha de referencia. Sin especificar acepta todas. */
	public static Specification<VisitAuthorizationEntity> withStatus(final VisitAuthorizationStatus status,
			final Instant now) {
		final Specification<VisitAuthorizationEntity> active = new AuthorizationIsActiveSpecification();
		final Specification<VisitAuthorizationEntity> started = new AuthorizationHasStartedSpecification(now);
		final Specification<VisitAuthorizationEntity> ended = new AuthorizationHasEndedSpecification(now);
		return switch (status) {
			case CURRENT -> active.and(started).and(ended.not());
			case UPCOMING -> active.and(started.not());
			case EXPIRED -> active.and(ended);
			case CANCELLED -> active.not();
			case UNSPECIFIED -> new AuthorizationAcceptAllSpecification();
		};
	}

	/** La propiedad indicada. La propiedad por defecto quiere decir sin filtro y acepta todas. */
	public static Specification<VisitAuthorizationEntity> ofProperty(final UUID propertyId) {
		return UUIDHelper.getUUIDHelper().isDefaultUUID(propertyId) ? new AuthorizationAcceptAllSpecification()
				: new AuthorizationOfPropertySpecification(propertyId);
	}

	/** Combina con "y" los cuatro filtros; los que traen su valor por defecto no descartan nada. */
	public static Specification<VisitAuthorizationEntity> matching(final VisitAuthorizationStatus status,
			final Instant from, final Instant to, final UUID propertyId, final Instant now) {
		return withStatus(status, now).and(new AuthorizationOverlapsRangeSpecification(from, to))
				.and(ofProperty(propertyId));
	}
}
