package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class AuthorizingResidentEntity implements AuthorizingPersonEntity{
	
	private final UUID id;
	private final String documentNumber;
	private final String fullName;
	private final boolean active;
	private final PropertyVisitEntity property;
	
	

	public AuthorizingResidentEntity(final UUID id, final String documentNumber, final String fullName, final boolean active,
			PropertyVisitEntity property) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.active = active;
		this.property = property;
	}

	public UUID getId() {
		return id;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean getActive() {
		return active;
	}

	public PropertyVisitEntity getProperty() {
		return property;
	}
}
