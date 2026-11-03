package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;

/**
 * Consulta de las autorizaciones de quien tiene la sesion. Los filtros son opcionales (nulo = sin filtro).
 * La persona sale de la sesion, nunca de la direccion: nadie puede pedir las autorizaciones de otro.
 * La pagina empieza en 0; si no se indica, es la primera con el tamano por defecto.
 */
public record ListVisitAuthorizationsDtoRequest(VisitAuthorizationStatus status, Instant from, Instant to,
		UUID propertyId, CurrentUser currentUser, int page, int size) {

	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int MAX_PAGE_SIZE = 50;

	private static final CurrentUser ANONYMOUS = new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(), "",
			Set.of(), UUIDHelper.getUUIDHelper().getDefault());

	/** Sin paginacion explicita: primera pagina con el tamano por defecto. */
	public ListVisitAuthorizationsDtoRequest(final VisitAuthorizationStatus status, final Instant from,
			final Instant to, final UUID propertyId, final CurrentUser currentUser) {
		this(status, from, to, propertyId, currentUser, 0, DEFAULT_PAGE_SIZE);
	}

	public ListVisitAuthorizationsDtoRequest {
		currentUser = ObjectHelper.getDefault(currentUser, ANONYMOUS);
	}
}
