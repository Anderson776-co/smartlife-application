package co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity;

import java.util.UUID;

public class UrbanizationUserEntity {
	
	private final UUID id;
	private final String name;
	
	

	public UrbanizationUserEntity(final UUID id, final String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
