package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity;

import java.util.UUID;

public class DwellingEntity {
	
	private UUID id;
	private String dwellingNumber;
	private String block;
	private UserOwnerEntity owner;
	private UrbanizationDwellingEntity urbanization;
	
	public DwellingEntity(final UUID id, final String dwellingNumber, final String block, final UserOwnerEntity owner,final UrbanizationDwellingEntity urbanization) {
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

	public UserOwnerEntity getOwner() {
		return owner;
	}

	public void setOwner(UserOwnerEntity owner) {
		this.owner = owner;
	}

	public UrbanizationDwellingEntity getUrbanization() {
		return urbanization;
	}

	public void setUrbanization(UrbanizationDwellingEntity urbanization) {
		this.urbanization = urbanization;
	}

	
	
	
}
