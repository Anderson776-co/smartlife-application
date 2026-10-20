package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class PropertyVisitEntity {
	
	private final UUID id;
	private final String location; // Location: Ubicacion -> Es un dato calculado que proviene del contexto de viviendas.
	private final AuthorizingOwnerEntity owner;
	
	
	
	public PropertyVisitEntity(final UUID id, final String location, final AuthorizingOwnerEntity owner) {
		this.id = id;
		this.location = location;
		this.owner = owner;
	}

	public UUID getId() {
		return id;
	}
	
	public String getLocation() {
		return location;
	}
	
	public AuthorizingOwnerEntity getOwner() {
		return owner;
	}
}
