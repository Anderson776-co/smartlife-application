package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "authorizing_owne")
public class AuthorizingOwnerEntityJpa {
	
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "document_number")
	private String documentNumber;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "is_active")
	private boolean active;
	
	

	public AuthorizingOwnerEntityJpa() {
	}

	public AuthorizingOwnerEntityJpa(final UUID id, final String documentNumber, final String fullName, final boolean active) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.active = active;
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

	public boolean getActive() {
		return active;
	}
}
