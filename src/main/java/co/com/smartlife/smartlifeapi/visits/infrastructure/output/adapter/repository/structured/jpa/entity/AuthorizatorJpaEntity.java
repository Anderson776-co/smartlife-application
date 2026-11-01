package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "authorizator",  schema = "visits")
public class AuthorizatorJpaEntity {
	
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "urbanization_id")
	private UUID urbanizationId;

	protected AuthorizatorJpaEntity() {}

	public AuthorizatorJpaEntity(final UUID id, final String fullName, final Boolean isActive, final UUID urbanizationId) {
		this.id = id;
		this.fullName = fullName;
		this.isActive = isActive;
		this.urbanizationId = urbanizationId;
	}

	public UUID getId() {
		return id;
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
