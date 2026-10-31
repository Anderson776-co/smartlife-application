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
		boolean notificationSent) {
}
