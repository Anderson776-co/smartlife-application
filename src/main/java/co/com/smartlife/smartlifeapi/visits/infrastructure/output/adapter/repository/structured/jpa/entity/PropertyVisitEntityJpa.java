package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

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
@Table(name = "property_visit")
public class PropertyVisitEntityJpa {
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "location")
	private String location;
	
	@ManyToOne
	@JoinColumn(name = "id_owner", referencedColumnName = "id")
	private AuthorizingOwnerEntityJpa owner;

	public PropertyVisitEntityJpa() {
	}

	public PropertyVisitEntityJpa(final UUID id, final String location, final AuthorizingOwnerEntityJpa owner) {
		this.id = id;
		this.location = location;
		this.owner = owner;
	}

	public UUID getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public AuthorizingOwnerEntityJpa getOwner() {
		return owner;
	}
}
