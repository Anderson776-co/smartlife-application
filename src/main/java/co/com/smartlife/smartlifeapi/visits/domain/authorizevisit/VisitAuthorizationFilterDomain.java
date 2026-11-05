package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

public record VisitAuthorizationFilterDomain(UUID authorizatorId, Boolean isActive, Instant from, Instant to,
		UUID propertyId, int page, int size) {
}
