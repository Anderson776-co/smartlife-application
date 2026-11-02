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
 */
public record ListVisitAuthorizationsDtoRequest(VisitAuthorizationStatus status, Instant from, Instant to,
        UUID propertyId, CurrentUser currentUser) {

    private static final CurrentUser ANONYMOUS = new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(), "",
            Set.of(), UUIDHelper.getUUIDHelper().getDefault());

    public ListVisitAuthorizationsDtoRequest {
        currentUser = ObjectHelper.getDefault(currentUser, ANONYMOUS);
    }
}
