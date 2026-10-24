package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "property_visit", schema = "visits")
public class PropertyVisitJpaEntity {
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "owner_id")
	private UUID ownerId;
	
	protected PropertyVisitJpaEntity() {}

	public PropertyVisitJpaEntity(final UUID id, final String location, final UUID ownerId) {
		this.id = id;
		this.location = location;
		this.ownerId = ownerId;
	}

	public UUID getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public UUID getOwnerId() {
		return ownerId;
	}

	
}
