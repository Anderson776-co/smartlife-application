package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

/**
 * Filtros opcionales de la consulta de autorizaciones. Un filtro nulo no se aplica.
 * from y to delimitan un rango de fechas: se devuelven las autorizaciones que se cruzan con el.
 */
public record VisitAuthorizationFilterDomain(VisitAuthorizationStatus status, Instant from, Instant to,
        UUID propertyId) {

    /** Sin ningun filtro. */
    public static VisitAuthorizationFilterDomain none() {
        return new VisitAuthorizationFilterDomain(null, null, null, null);
    }
}
