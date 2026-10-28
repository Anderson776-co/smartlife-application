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
		PropertyVisitDomain property,
		AuthorizatorDomain authorizator,
		VisitorDomain visitor,
		VisitReasonDomain visitReason,
		boolean notificationSent) {

	public VisitAuthorizationDomain withNotificationSent(final boolean sent) {
		return new VisitAuthorizationDomain(id, authorizationStartDate, authorizationEndDate, registerDate,
				observation, qrCode, fileReference, property, authorizator, visitor, visitReason, sent);
	}
}
