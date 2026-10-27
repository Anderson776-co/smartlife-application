package co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity;

import java.util.UUID;

public class UrbanizationEntity {
	
	private UUID id;
	private String name;

	public UrbanizationEntity(final UUID id, final String name) {
		setId(id);
		setName(name);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
