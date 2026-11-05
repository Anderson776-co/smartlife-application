package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

/** Una autorizacion tal como se CONSULTA en el listado: solo lo que se muestra, con el estado en isActive. */
public record VisitAuthorizationSummaryDomain(UUID id, Instant authorizationStartDate, Instant authorizationEndDate,
		String propertyLocation, String visitReason, boolean documentAttached, String visitorDocumentNumber,
		String visitorFullName, String visitorEmail, String vehiclePlate, Boolean isActive) {
}
