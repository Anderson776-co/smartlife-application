package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

public record ResidenceDomain(UUID id, String location, UUID ownerId, UUID urbanizationId) {
}
