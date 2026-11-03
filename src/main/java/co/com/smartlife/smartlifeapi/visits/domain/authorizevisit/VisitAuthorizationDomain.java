package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

/**
 * Una autorizacion de visita. La misma clase sirve para autorizar y para consultar.
 * Al consultar se llenan ademas los datos que se muestran: ubicacion de la propiedad, motivo de la visita y estado.
 */
public record VisitAuthorizationDomain(
		UUID id,
		Instant authorizationStartDate,
		Instant authorizationEndDate,
		Instant registerDate,
		String observation,
		String qrCode,
		String fileReference,
		AttachedDocumentDomain attachedDocument,
		UUID propertyId,
		UUID visitReasonId,
		AuthorizatorDomain authorizator,
		VisitorDomain visitor,
		boolean notificationSent,
		String propertyLocation,
		String visitReasonName,
		VisitAuthorizationStatus status) {

	/** Al autorizar no hace falta la ubicacion, el nombre del motivo ni el estado: quedan sin llenar. */
	public VisitAuthorizationDomain(final UUID id, final Instant authorizationStartDate,
			final Instant authorizationEndDate, final Instant registerDate, final String observation,
			final String qrCode, final String fileReference, final AttachedDocumentDomain attachedDocument,
			final UUID propertyId, final UUID visitReasonId, final AuthorizatorDomain authorizator,
			final VisitorDomain visitor, final boolean notificationSent) {
		this(id, authorizationStartDate, authorizationEndDate, registerDate, observation, qrCode, fileReference,
				attachedDocument, propertyId, visitReasonId, authorizator, visitor, notificationSent, null, null, null);
	}
}
