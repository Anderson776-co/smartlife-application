package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

/**
 * Lo que se muestra de una autorizacion cuando se consulta: fechas, visitante, vivienda, motivo y estado.
 * No incluye el contenido del documento adjunto, solo si lo tiene.
 */
public record VisitAuthorizationSummaryDomain(UUID id, Instant authorizationStartDate, Instant authorizationEndDate,
		Instant registerDate, String observation, String qrCode, String propertyLocation, String visitReason,
		boolean documentAttached, String visitorDocumentNumber, String visitorFullName, String visitorEmail,
		String vehiclePlate, VisitAuthorizationStatus status) {
}
