package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

/**
 * Estado de una autorizacion de visita segun la fecha de referencia.
 * <ul>
 * <li>UPCOMING: activa y todavia no ha empezado.</li>
 * <li>CURRENT: activa, ya empezo y todavia no termina.</li>
 * <li>EXPIRED: activa y ya termino.</li>
 * <li>CANCELLED: desactivada (no esta activa).</li>
 * <li>UNSPECIFIED: valor por defecto, nunca nulo. Como filtro no filtra por estado; como estado quiere decir que
 * todavia no se calculo.</li>
 * </ul>
 */
public enum VisitAuthorizationStatus {
	UPCOMING, CURRENT, EXPIRED, CANCELLED, UNSPECIFIED;

	/** Un estado real de la autorizacion: todos menos el valor por defecto. */
	public boolean isSpecified() {
		return this != UNSPECIFIED;
	}
}
