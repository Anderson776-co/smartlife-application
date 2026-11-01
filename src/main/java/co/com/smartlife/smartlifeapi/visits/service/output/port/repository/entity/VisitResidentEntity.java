package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class VisitResidentEntity{
	
	private final UUID id;
	private final PropertyVisitEntity property;

	public VisitResidentEntity(final UUID id, final PropertyVisitEntity property) {
		this.id = id;
		this.property = property;
	}

	public UUID getId() {
		return id;
	}

	public PropertyVisitEntity getProperty() {
		return property;
	}
}
