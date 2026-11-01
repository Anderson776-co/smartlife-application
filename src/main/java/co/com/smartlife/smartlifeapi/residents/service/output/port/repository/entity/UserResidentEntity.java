package co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity;

import java.util.UUID;

public class UserResidentEntity {
	private final UUID id;
	private final String documentNumber;
	private final String fullName;
	private final Boolean isActive;
	private final UUID urbanizationId;

	public UserResidentEntity(final UUID id,final String documentNumber, final String fullName, final Boolean isActive, final UUID urbanizationId) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.isActive = isActive;
		this.urbanizationId = urbanizationId;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public UUID getUrbanizationId() {
		return urbanizationId;
	}

	

}
