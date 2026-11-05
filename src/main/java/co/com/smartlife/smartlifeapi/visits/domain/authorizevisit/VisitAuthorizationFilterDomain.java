package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.time.Instant;
import java.util.UUID;

/** Que autorizaciones consultar. isActive, from, to y propertyId son opcionales: null significa "sin filtro". */
public record VisitAuthorizationFilterDomain(UUID authorizatorId, Boolean isActive, Instant from, Instant to,
		UUID propertyId, int page, int size) {
}
