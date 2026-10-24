package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class AuthorizatorEntity {
	private final UUID id;
	private final String fullName;
	private final Boolean isActive;
	private final UUID urbanizationId;

	public AuthorizatorEntity(final UUID id, final String fullName, final Boolean isActive, final UUID urbanizationId) {
		this.id = id;
		this.fullName = fullName;
		this.isActive = isActive;
		this.urbanizationId = urbanizationId;
	}

	public UUID getId() {
		return id;
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
