package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

/** Pasa el resumen de una autorizacion del dominio a la respuesta de la consulta (el codigo QR sale como qrUrl). */
public final class VisitAuthorizationSummaryDtoMapper {

	private VisitAuthorizationSummaryDtoMapper() {
	}

	public static VisitAuthorizationSummaryDtoResponse toDto(final VisitAuthorizationSummaryDomain summary) {
		return new VisitAuthorizationSummaryDtoResponse(summary.id(), summary.authorizationStartDate(),
				summary.authorizationEndDate(), summary.registerDate(), summary.observation(), summary.qrCode(),
				summary.propertyLocation(), summary.visitReason(), summary.documentAttached(),
				summary.visitorDocumentNumber(), summary.visitorFullName(), summary.visitorEmail(),
				summary.vehiclePlate(), summary.status());
	}
}
