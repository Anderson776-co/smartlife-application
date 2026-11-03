package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

public record UserResidentDomain(UUID id, String documentNumber, String fullName, Boolean active, UUID urbanizationId) {
}
