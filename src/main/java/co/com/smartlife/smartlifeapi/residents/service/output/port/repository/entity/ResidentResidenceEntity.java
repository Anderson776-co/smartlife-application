package co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity;

import java.util.UUID;

public class ResidentResidenceEntity {
	
	private UUID id;
    private ResidenceEntity residence;
    private ResidentEntity resident;
    private Boolean isActive;

    public ResidentResidenceEntity(
    		final UUID id,
            final ResidenceEntity residence,
            final ResidentEntity resident,
            final Boolean isActive) {

        setId(id);
        setResidence(residence);
        setResident(resident);
        setIsActive(isActive);
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ResidenceEntity getResidence() {
		return residence;
	}

	public void setResidence(ResidenceEntity residence) {
		this.residence = residence;
	}

	public ResidentEntity getResident() {
		return resident;
	}

	public void setResident(ResidentEntity resident) {
		this.resident = resident;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}