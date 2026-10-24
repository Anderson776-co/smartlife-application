package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import com.google.errorprone.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "urbanization", schema = "dwellings")
public class UrbanizationDwellingJpaEntity {
	
	@Id
	@Column(name = "id")
	private final UUID id;
	
	@Column(name = "name")
	private final String name;

	public UrbanizationDwellingJpaEntity(final UUID id, final String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
