package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

public record ResidentResidenceDomain(UUID id, ResidenceDomain residence, ResidentDomain resident, Boolean active) {

	public ResidentResidenceDomain withActive(final Boolean newActive) {
		return new ResidentResidenceDomain(id, residence, resident, newActive);
	}
}
