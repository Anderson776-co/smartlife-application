package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "apartment", schema = "dwellings")
public class ApartmentJpaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "floor")
	private int floor;
	
	@Column(name = "tower")
	private String tower;
	
	@OneToOne
	@JoinColumn(name = "id_dwelling")
	private DwellingJpaEntity dwelling;
	
	public ApartmentJpaEntity(final UUID id, final int floor, final String tower, final DwellingJpaEntity dwelling) {
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

	public DwellingJpaEntity getDwelling() {
		return dwelling;
	}

	public void setDwelling(DwellingJpaEntity dwelling) {
		this.dwelling = dwelling;
	}
	
}
