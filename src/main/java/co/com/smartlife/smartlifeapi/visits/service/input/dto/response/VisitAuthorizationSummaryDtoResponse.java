package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;

/** Una autorizacion tal como se muestra al consultarla. No incluye el contenido del documento adjunto. */
public record VisitAuthorizationSummaryDtoResponse(UUID authorizationId, Instant authorizationStartDate,
		Instant authorizationEndDate, Instant registerDate, String observation, String qrUrl, String propertyLocation,
		String visitReason, boolean documentAttached, String visitorDocumentNumber, String visitorFullName,
		String visitorEmail, String vehiclePlate, VisitAuthorizationStatus status) {
}
