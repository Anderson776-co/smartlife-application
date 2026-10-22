package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_reason")
public class VisitReasonEntityJpa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@Column(name = "name")
	private String name;
	
	
	
	public VisitReasonEntityJpa() {
	}

	public VisitReasonEntityJpa(final UUID id, final String name) {
		setId(id);
		setName(name);
	}
	
	public UUID getId() {
		return id;
	}
	
	private void setId(final UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	private void setName(final String name) {
		this.name = name;
	}

}
