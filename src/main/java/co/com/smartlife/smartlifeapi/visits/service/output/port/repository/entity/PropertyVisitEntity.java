package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class PropertyVisitEntity {
	
	public static final String ID = "id";
	public static final String LOCATION = "location";
	
	private final UUID id;
	private final String location;
	
	public PropertyVisitEntity(final UUID id, final String location) {
		this.id = id;
		this.location = location;
	}

	public UUID getId() {
		return id;
	}
	
	public String getLocation() {
		return location;
	}
}
