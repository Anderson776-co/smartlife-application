package co.com.smartlife.smartlifeapi.visits.domain;

import java.util.UUID;

public record VisitReasonDomain(UUID id, String name, boolean requiresDocument) {

}
