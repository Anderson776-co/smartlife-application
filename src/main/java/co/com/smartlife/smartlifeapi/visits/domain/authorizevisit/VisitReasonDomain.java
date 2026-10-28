package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

public record VisitReasonDomain(UUID id, String name, Boolean requiresDocument) {
	
	public boolean documentIsRequired() {
		return Boolean.TRUE.equals(requiresDocument);
	}

}
