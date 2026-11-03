package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

/** La relacion de un residente con una residencia, tal como la ve el dominio. */
public record ResidentResidenceDomain(UUID id, ResidenceDomain residence, ResidentDomain resident, Boolean active) {

	public ResidentResidenceDomain withActive(final Boolean newActive) {
		return new ResidentResidenceDomain(id, residence, resident, newActive);
	}
}
