package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity;

import java.util.UUID;

public class HouseEntity {
	private UUID id;
	private String streetNumber;
	private DwellingEntity dwelling;
	
	public HouseEntity(final UUID id, final String streetNumber, final DwellingEntity dwelling) {
		setId(id);
		setStreetNumber(streetNumber);
		setDwelling(dwelling);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public DwellingEntity getDwelling() {
		return dwelling;
	}

	public void setDwelling(DwellingEntity dwelling) {
		this.dwelling = dwelling;
	}
	
	
}
