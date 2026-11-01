package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import com.google.errorprone.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "user_resident", schema = "residents")
public class UserResidentJpaEntity {
	
	@Id
	@Column(name = "id")
	private final UUID id;
	
	@Column(name = "document_number")
	private final String documentNumber;
	
	@Column(name = "full_name")
	private final String fullName;
	
	@Column(name = "is_active")
	private final Boolean isActive;
	
	@Column(name = "urbanization_id")
	private final UUID urbanizationId;

	public UserResidentJpaEntity(final UUID id,final String documentNumber, final String fullName, final Boolean isActive, final UUID urbanizationId) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.isActive = isActive;
		this.urbanizationId = urbanizationId;
	}

	public UUID getId() {
		return id;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public UUID getUrbanizationId() {
		return urbanizationId;
	}

	

}
