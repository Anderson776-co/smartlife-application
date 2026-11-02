package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

/**
 * Estado de una autorizacion de visita segun la fecha de referencia.
 * <ul>
 * <li>UPCOMING: activa y todavia no ha empezado.</li>
 * <li>CURRENT: activa, ya empezo y todavia no termina.</li>
 * <li>EXPIRED: activa y ya termino.</li>
 * <li>CANCELLED: desactivada (no esta activa).</li>
 * </ul>
 */
public enum VisitAuthorizationStatus {
    UPCOMING, CURRENT, EXPIRED, CANCELLED
}
