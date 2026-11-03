package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

public record ResidentResidenceStatusChangeDomain(UUID residentResidenceId, Boolean active) {
}
