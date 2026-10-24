package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity;

import java.util.UUID;

public class UrbanizationDwellingEntity {
	
	private final UUID id;
	private final String name;

	public UrbanizationDwellingEntity(final UUID id, final String name) {
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
