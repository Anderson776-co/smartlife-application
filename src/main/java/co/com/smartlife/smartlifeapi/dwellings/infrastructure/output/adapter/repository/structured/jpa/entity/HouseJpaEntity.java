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
@Table(name = "house", schema = "dwellings")
public class HouseJpaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "street_number")
	private String streetNumber;
	
	@OneToOne
	@JoinColumn(name = "id_dwelling")
	private DwellingJpaEntity dwelling;
	
	public HouseJpaEntity(final UUID id, final String streetNumber, final DwellingJpaEntity dwelling) {
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

	public DwellingJpaEntity getDwelling() {
		return dwelling;
	}

	public void setDwelling(DwellingJpaEntity dwelling) {
		this.dwelling = dwelling;
	}
	
	
}
