package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

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

	private static final String EMPTY = "";

	public VisitAuthorizationDomain(final UUID id, final Instant authorizationStartDate,
			final Instant authorizationEndDate, final Instant registerDate, final String observation,
			final String qrCode, final String fileReference, final AttachedDocumentDomain attachedDocument,
			final UUID propertyId, final UUID visitReasonId, final AuthorizatorDomain authorizator,
			final VisitorDomain visitor, final boolean notificationSent) {
		this(id, authorizationStartDate, authorizationEndDate, registerDate, observation, qrCode, fileReference,
				attachedDocument, propertyId, visitReasonId, authorizator, visitor, notificationSent, EMPTY, EMPTY,
				VisitAuthorizationStatus.UNSPECIFIED);
	}
}
