package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class PropertyVisitEntity {
	
	private final UUID id;
	private final String location; // Location: Ubicacion -> Es un dato calculado que proviene del contexto de viviendas.
	private final UUID ownerId;
	
	
	
	public PropertyVisitEntity(final UUID id, final String location, final UUID ownerId) {
		this.id = id;
		this.location = location;
		this.ownerId = ownerId;
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
}
