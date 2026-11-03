package co.com.smartlife.smartlifeapi.urbanization.domain;

import java.util.UUID;

/** Una urbanizacion tal como la ve el dominio. */
public record UrbanizationDomain(UUID id, String name) {

	public UrbanizationDomain withName(final String newName) {
		return new UrbanizationDomain(id, newName);
	}
}
