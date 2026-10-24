package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity;

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
@Table(name = "resident_dwelling")
public class ResidentResidenceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_residence")
    private ResidenceJpaEntity residence;

    @ManyToOne
    @JoinColumn(name = "id_resident")
    private ResidentJpaEntity resident;
    
    @Column(name = "is_active")
    private Boolean isActive;

    public ResidentResidenceJpaEntity(
    		final UUID id,
            final ResidenceJpaEntity residence,
            final ResidentJpaEntity resident,
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

	public ResidenceJpaEntity getResidence() {
		return residence;
	}

	public void setResidence(ResidenceJpaEntity residence) {
		this.residence = residence;
	}

	public ResidentJpaEntity getResident() {
		return resident;
	}

	public void setResident(ResidentJpaEntity resident) {
		this.resident = resident;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	  
}
