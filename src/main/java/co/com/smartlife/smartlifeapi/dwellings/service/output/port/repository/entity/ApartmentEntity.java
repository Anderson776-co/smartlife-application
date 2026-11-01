package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity;

import java.util.UUID;

public class ApartmentEntity {
	private UUID id;
	private int floor;
	private String tower;
	private DwellingEntity dwelling;
	
	public ApartmentEntity(final UUID id, final int floor, final String tower, final DwellingEntity dwelling) {
		setId(id);
		setFloor(floor);
		setTower(tower);
		setDwelling(dwelling);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getTower() {
		return tower;
	}

	public void setTower(String tower) {
		this.tower = tower;
	}

	public DwellingEntity getDwelling() {
		return dwelling;
	}

	public void setDwelling(DwellingEntity dwelling) {
		this.dwelling = dwelling;
	}
	
}
