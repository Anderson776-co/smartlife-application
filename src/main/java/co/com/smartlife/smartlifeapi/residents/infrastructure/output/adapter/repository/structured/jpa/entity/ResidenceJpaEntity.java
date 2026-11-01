package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import com.google.errorprone.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "residence", schema = "residents")
public class ResidenceJpaEntity {
	
	@Id
	@Column(name = "id")
	private final UUID id;
	
	@Column(name = "location")
	private final String location; // Location: Ubicacion -> Es un dato calculado que proviene del contexto de viviendas.
	
	@Column(name = "id_owner")
	private final UUID ownerId;
	
	@Column(name = "id_urbanization")
	private final UUID urbanizationId;
	
	public ResidenceJpaEntity(final UUID id, final String location, final UUID ownerId, final UUID urbanizationId) {
		this.id = id;
		this.location = location;
		this.ownerId = ownerId;
		this.urbanizationId = urbanizationId;
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

	public UUID getUrbanizationId() {
		return urbanizationId;
	}
}
