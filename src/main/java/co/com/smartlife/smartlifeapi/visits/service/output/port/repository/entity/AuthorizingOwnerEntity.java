package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class AuthorizingOwnerEntity implements AuthorizingPersonEntity {
	
	private final UUID id;
	private final String documentNumber;
	private final String fullName;
	private final boolean active;

	public AuthorizingOwnerEntity(final UUID id, final String documentNumber, final String fullName, final boolean active) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.active = active;
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
}
