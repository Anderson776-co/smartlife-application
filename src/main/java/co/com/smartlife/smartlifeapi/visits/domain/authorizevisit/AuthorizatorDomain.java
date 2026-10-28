package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

public record AuthorizatorDomain(UUID id, String fullName, Boolean isActive, UUID urbanizationId) { }
