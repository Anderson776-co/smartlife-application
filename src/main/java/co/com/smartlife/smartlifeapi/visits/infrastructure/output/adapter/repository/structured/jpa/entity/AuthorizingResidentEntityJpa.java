package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "authorizing_owne")
public class AuthorizingResidentEntityJpa {
	
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "document_number")
	private String documentNumber;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "is_active")
	private boolean active;
	
	@ManyToOne
	@JoinColumn(name = "id_property",referencedColumnName = "id")
	private PropertyVisitEntityJpa property;
	
	

	public AuthorizingResidentEntityJpa() {
	}

	public AuthorizingResidentEntityJpa(final UUID id, final String documentNumber, final String fullName, final boolean active, PropertyVisitEntityJpa property) {
		this.id = id;
		this.documentNumber = documentNumber;
		this.fullName = fullName;
		this.active = active;
		this.property = property;
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

	public PropertyVisitEntityJpa getProperty() {
		return property;
	}
}
