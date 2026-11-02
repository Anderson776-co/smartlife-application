package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

/**
 * Arma los filtros de la consulta de autorizaciones combinando Specification con and, or y not.
 * Los estados se componen a partir de tres condiciones simples: activa, ya empezo y ya termino.
 */
public final class AuthorizationSpecifications {

    private AuthorizationSpecifications() {
    }

    /** Estado de la autorizacion segun la fecha de referencia. */
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
        };
    }

    /**
     * Combina con "y" solo los filtros que vienen informados. Sin ningun filtro, acepta todas las autorizaciones.
     */
    public static Specification<VisitAuthorizationEntity> matching(final VisitAuthorizationStatus status,
            final Instant from, final Instant to, final UUID propertyId, final Instant now) {
        Specification<VisitAuthorizationEntity> result = acceptAll();
        if (status != null) {
            result = result.and(withStatus(status, now));
        }
        if (from != null || to != null) {
            result = result.and(new AuthorizationOverlapsRangeSpecification(from, to));
        }
        if (propertyId != null) {
            result = result.and(new AuthorizationOfPropertySpecification(propertyId));
        }
        return result;
    }

    private static Specification<VisitAuthorizationEntity> acceptAll() {
        return new Specification<>() {
            @Override
            public boolean isSatisfiedBy(final VisitAuthorizationEntity data) {
                return data != null;
            }
        };
    }
}
