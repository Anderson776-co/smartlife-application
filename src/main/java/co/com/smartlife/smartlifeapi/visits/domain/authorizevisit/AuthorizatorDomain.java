package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.Set;
import java.util.UUID;

public record AuthorizatorDomain(UUID id, UUID urbanizationId,
		Set<String> roles) {

	private static final String OWNER_ROLE = "OWNER";
	private static final String RESIDENT_ROLE = "RESIDENT";

	public AuthorizatorDomain {
		roles = roles == null ? Set.of() : Set.copyOf(roles);
	}

	public boolean isOwner() {
		return roles.contains(OWNER_ROLE);
	}

	public boolean isResident() {
		return roles.contains(RESIDENT_ROLE);
	}

}
