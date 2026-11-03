package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.UUID;

public record UserOwnerDomain(UUID id, String documentNumber, String fullName, Boolean active, UUID urbanizationId) {
}
