package co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity;

import java.util.UUID;

public class ResidenceEntity {
	
	private final UUID id;
	private final String location; // Location: Ubicacion -> Es un dato calculado que proviene del contexto de viviendas.
	private final UUID ownerId;
	private final UUID urbanizationId;
	
	public ResidenceEntity(final UUID id, final String location, final UUID ownerId, final UUID urbanizationId) {
		this.id = id;
		this.location = location;
		this.ownerId = ownerId;
		this.urbanizationId = urbanizationId;
	}

	public UUID getId() {
		return id;
	}
	
	public String getLocation() {
		return location;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	public UUID getUrbanizationId() {
		return urbanizationId;
	}
}
