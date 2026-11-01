package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dwelling", schema = "dwellings")
public class DwellingJpaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "dwelling_number")
	private String dwellingNumber;
	
	@Column(name = "block")
	private String block;
	
	@ManyToOne
	@JoinColumn(name = "id_owner")
	private UserOwnerJpaEntity owner;
	
	@ManyToOne
	@JoinColumn(name = "id_urbanization")
	private UrbanizationDwellingJpaEntity urbanization;
	
	public DwellingJpaEntity(final UUID id, final String dwellingNumber, final String block, final UserOwnerJpaEntity owner,final UrbanizationDwellingJpaEntity urbanization) {
		setId(id);
		setDwellingNumber(dwellingNumber);
		setBlock(block);
		setOwner(owner);
		setUrbanization(urbanization);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDwellingNumber() {
		return dwellingNumber;
	}

	public void setDwellingNumber(String dwellingNumber) {
		this.dwellingNumber = dwellingNumber;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public UserOwnerJpaEntity getOwner() {
		return owner;
	}

	public void setOwner(UserOwnerJpaEntity owner) {
		this.owner = owner;
	}

	public UrbanizationDwellingJpaEntity getUrbanization() {
		return urbanization;
	}

	public void setUrbanization(UrbanizationDwellingJpaEntity urbanization) {
		this.urbanization = urbanization;
	}
}
